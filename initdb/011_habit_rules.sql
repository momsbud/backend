-- 011_habit_rules.sql
-- Generic rule engine tables for habit assignment.
-- Works for Maternity today, other LOBs tomorrow.

-- =========================
-- habit_rule
-- =========================
create table if not exists habit_rule (
    id varchar(26) primary key,

    name text not null,
    lob text not null,                 -- e.g. 'Maternity', 'Agriculture'
    active boolean not null default true,
    priority int not null default 0,

    match jsonb not null default '{}'::jsonb,     -- {all:[], any:[], not:[]}
    action jsonb not null default '{}'::jsonb,    -- {assignHabits:[...], assignmentWindowDays:.., dedupeKey:..}

    valid_from timestamptz null,
    valid_to timestamptz null,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    created_by varchar(26) null,
    updated_by varchar(26) null,
    is_deleted boolean not null default false,
    metadata jsonb not null default '{}'::jsonb
);

create index if not exists idx_habit_rule_lob_active
    on habit_rule (lob, active)
    where is_deleted = false;

create index if not exists idx_habit_rule_priority
    on habit_rule (priority desc)
    where is_deleted = false;

create index if not exists idx_habit_rule_match_gin
    on habit_rule using gin (match)
    where is_deleted = false;

-- =========================
-- habit_assignment
-- =========================
create table if not exists habit_assignment (
    id varchar(26) primary key,

    user_id varchar(26) not null,
    habit_id varchar(26) not null,
    rule_id varchar(26) null,

    status varchar(20) not null default 'ACTIVE', -- ACTIVE | ENDED | PAUSED
    assigned_from timestamptz not null default now(),
    assigned_until timestamptz null,

    -- helps dedupe cross-runs; can be set to rule.action.dedupeKey or derived value
    dedupe_key text null,

    -- reason/explainability
    reason text null,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    created_by varchar(26) null,
    updated_by varchar(26) null,
    is_deleted boolean not null default false,
    metadata jsonb not null default '{}'::jsonb
);

create index if not exists idx_habit_assignment_user_status
    on habit_assignment (user_id, status)
    where is_deleted = false;

create index if not exists idx_habit_assignment_rule
    on habit_assignment (rule_id)
    where is_deleted = false;

-- Prevent duplicate ACTIVE assignment for same user + habit.
create unique index if not exists uq_habit_assignment_active_user_habit
    on habit_assignment (user_id, habit_id)
    where status = 'ACTIVE' and is_deleted = false;

-- Prevent duplicate ACTIVE assignment for same user + dedupe_key (when provided).
create unique index if not exists uq_habit_assignment_active_user_dedupe
    on habit_assignment (user_id, dedupe_key)
    where dedupe_key is not null and status = 'ACTIVE' and is_deleted = false;
