-- 006_tracking.sql

-- Master list of metric types
create table if not exists metric_type (
  id          varchar(26) primary key,
  code        varchar(64)  not null unique,         -- e.g., weight_kg
  label       varchar(128) not null,                -- e.g., Weight (kg)
  unit        varchar(32)  not null,                -- e.g., kg (varchar, not enum)
  enabled     boolean not null default true,

  metadata    jsonb not null default '{}'::jsonb,

  created_at  timestamptz not null default now(),
  updated_at  timestamptz not null default now(),
  created_by  varchar(26),
  updated_by  varchar(26),
  is_deleted  boolean not null default false
);

-- User metric logs (time series)
create table if not exists user_metric (
  id          varchar(26) primary key,
  user_id     varchar(26) not null,
  type_code   varchar(64) not null,                 -- reference metric_type.code (by code, not FK for agility)
  value       numeric(18,6) not null,               -- BigDecimal in code
  recorded_at timestamptz   not null,               -- OffsetDateTime in code
  timezone    varchar(64)   not null default 'Asia/Kolkata',
  notes       varchar(500),

  metadata    jsonb not null default '{}'::jsonb,

  created_at  timestamptz not null default now(),
  updated_at  timestamptz not null default now(),
  created_by  varchar(26),
  updated_by  varchar(26),
  is_deleted  boolean not null default false
);

create index if not exists idx_metric_type_enabled on metric_type(enabled) where is_deleted = false;
create index if not exists idx_user_metric_user_time on user_metric(user_id, recorded_at) where is_deleted = false;
create index if not exists idx_user_metric_type on user_metric(type_code) where is_deleted = false;
