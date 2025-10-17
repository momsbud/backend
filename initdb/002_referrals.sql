-- 002_referrals.sql
-- Referrals MVP: referral_code & referral_attribution

-- ensure schema exists if you're using any (omit if public)
-- CREATE SCHEMA IF NOT EXISTS public;

-- referral_code: generated/claimed by DOCTOR users
CREATE TABLE IF NOT EXISTS referral_code (
  id              varchar(26) PRIMARY KEY,                 -- ULID
  doctor_user_id  varchar(26) NOT NULL REFERENCES users(id),
  code            varchar(32) NOT NULL UNIQUE,             -- human code
  active          boolean NOT NULL DEFAULT true,
  created_at      timestamptz NOT NULL DEFAULT now(),
  updated_at      timestamptz NOT NULL DEFAULT now(),
  created_by      varchar(26),
  updated_by      varchar(26),
  is_deleted      boolean NOT NULL DEFAULT false,
  metadata        jsonb NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS referral_code_doctor_idx ON referral_code (doctor_user_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS referral_code_active_idx ON referral_code (active) WHERE is_deleted = false;

-- referral_attribution: a user applied a code
CREATE TABLE IF NOT EXISTS referral_attribution (
  id              varchar(26) PRIMARY KEY,                 -- ULID
  code_id         varchar(26) NOT NULL REFERENCES referral_code(id),
  user_id         varchar(26) NOT NULL REFERENCES users(id),
  attributed_at   timestamptz NOT NULL DEFAULT now(),
  created_at      timestamptz NOT NULL DEFAULT now(),
  updated_at      timestamptz NOT NULL DEFAULT now(),
  created_by      varchar(26),
  updated_by      varchar(26),
  is_deleted      boolean NOT NULL DEFAULT false,
  metadata        jsonb NOT NULL DEFAULT '{}'::jsonb,
  CONSTRAINT referral_attribution_uniq UNIQUE (code_id, user_id)
);

CREATE INDEX IF NOT EXISTS referral_attr_user_idx ON referral_attribution (user_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS referral_attr_code_idx ON referral_attribution (code_id) WHERE is_deleted = false;
