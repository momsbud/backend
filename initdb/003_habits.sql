-- 003_habits.sql (no enums)

CREATE TABLE IF NOT EXISTS habit (
    id           varchar(26) PRIMARY KEY,
    user_id      varchar(26) NOT NULL REFERENCES users(id),
    title        varchar(120) NOT NULL,
    description  varchar(500),
    frequency    varchar(16) NOT NULL DEFAULT 'DAILY',
    days_of_week int[] NULL,
    time_of_day  time with time zone NULL,
    timezone     varchar(64) NULL,
    active       boolean NOT NULL DEFAULT true,
    habit_type   varchar(32) NOT NULL DEFAULT 'GENERIC',
    tags         text[] NULL,
    metadata     jsonb NOT NULL DEFAULT '{}'::jsonb,
    created_at   timestamptz NOT NULL DEFAULT now(),
    updated_at   timestamptz NOT NULL DEFAULT now(),
    created_by   varchar(100),
    updated_by   varchar(100),
    is_deleted   boolean NOT NULL DEFAULT false
);

CREATE INDEX IF NOT EXISTS habit_user_active_idx
  ON habit (user_id) WHERE active = true AND is_deleted = false;

CREATE TABLE IF NOT EXISTS user_habit_log (
  id            varchar(26) PRIMARY KEY,
  habit_id      varchar(26) NOT NULL REFERENCES habit(id),
  user_id       varchar(26) NOT NULL REFERENCES users(id),
  log_date      date NOT NULL,
  logged_at     timestamptz NOT NULL DEFAULT now(),
  status        varchar(16) NOT NULL,                -- DONE | SKIPPED
  notes         varchar(500),

  metadata      jsonb NOT NULL DEFAULT '{}'::jsonb,
  created_at    timestamptz NOT NULL DEFAULT now(),
  updated_at    timestamptz NOT NULL DEFAULT now(),
  created_by    varchar(26),
  updated_by    varchar(26),
  is_deleted    boolean NOT NULL DEFAULT false,

  CONSTRAINT user_habit_log_unique UNIQUE (habit_id, user_id, log_date)
);
CREATE INDEX IF NOT EXISTS uhl_user_date_idx
  ON user_habit_log (user_id, log_date) WHERE is_deleted = false;
