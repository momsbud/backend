-- ==== Enums ====
CREATE TYPE user_type AS ENUM ('CUSTOMER','DOCTOR','AGENT','ADMIN','SYSTEM');
CREATE TYPE otp_state AS ENUM ('SENT','VERIFIED','EXPIRED','FAILED');

-- ==== users ====
CREATE TABLE IF NOT EXISTS users (
  id               varchar(26) PRIMARY KEY,                 -- ULID string
  phone            varchar(20) UNIQUE,
  email            varchar(255),
  user_type        user_type NOT NULL DEFAULT 'CUSTOMER',
  status           varchar(16) NOT NULL DEFAULT 'ACTIVE',
  auth_provider    varchar(32) NOT NULL DEFAULT 'OTP',
  last_login_at    timestamptz,
  metadata         jsonb NOT NULL DEFAULT '{}'::jsonb,
  created_at       timestamptz NOT NULL DEFAULT now(),
  updated_at       timestamptz NOT NULL DEFAULT now(),
  created_by       varchar(26),
  updated_by       varchar(26),
  is_deleted       boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email_lower
  ON users (lower(email))
  WHERE email IS NOT NULL;

-- ==== user_profile ====
CREATE TABLE IF NOT EXISTS user_profile (
  id          varchar(26) PRIMARY KEY,
  user_id     varchar(26) NOT NULL UNIQUE REFERENCES users(id),
  full_name   text,
  dob         date,
  tz          text DEFAULT 'Asia/Kolkata',
  locale      text DEFAULT 'en-IN',
  metadata    jsonb NOT NULL DEFAULT '{}'::jsonb,
  created_at  timestamptz NOT NULL DEFAULT now(),
  updated_at  timestamptz NOT NULL DEFAULT now(),
  created_by  varchar(26),
  updated_by  varchar(26),
  is_deleted  boolean NOT NULL DEFAULT false
);

-- ==== doctor_profile ====
CREATE TABLE IF NOT EXISTS doctor_profile (
  id                   varchar(26) PRIMARY KEY,
  user_id              varchar(26) NOT NULL UNIQUE REFERENCES users(id),
  registration_no      text,
  speciality           text,
  clinic_org_id        varchar(26),
  years_of_experience  int,
  about_md             text,
  metadata             jsonb NOT NULL DEFAULT '{}'::jsonb,
  created_at           timestamptz NOT NULL DEFAULT now(),
  updated_at           timestamptz NOT NULL DEFAULT now(),
  created_by           varchar(26),
  updated_by           varchar(26),
  is_deleted           boolean NOT NULL DEFAULT false
);

-- ==== otp_attempt ====
CREATE TABLE IF NOT EXISTS otp_attempt (
  id                 varchar(26) PRIMARY KEY,
  user_id            varchar(26) REFERENCES users(id),
  phone              varchar(20) NOT NULL,
  code_hash          text NOT NULL,
  state              otp_state NOT NULL DEFAULT 'SENT',
  expires_at         timestamptz NOT NULL,
  ip                 inet,
  device_fingerprint text,
  fail_reason        text,
  metadata           jsonb NOT NULL DEFAULT '{}'::jsonb,
  created_at         timestamptz NOT NULL DEFAULT now(),
  updated_at         timestamptz NOT NULL DEFAULT now(),
  created_by         varchar(26),
  updated_by         varchar(26),
  is_deleted         boolean NOT NULL DEFAULT false
);
CREATE INDEX IF NOT EXISTS otp_attempt_phone_idx ON otp_attempt (phone, created_at DESC);

-- ==== user_session ====
CREATE TABLE IF NOT EXISTS user_session (
  id                 varchar(26) PRIMARY KEY,
  user_id            varchar(26) NOT NULL REFERENCES users(id),
  jti                uuid NOT NULL,
  created_at         timestamptz NOT NULL DEFAULT now(),
  last_seen_at       timestamptz NOT NULL DEFAULT now(),
  first_ip           inet,
  last_ip            inet,
  device_fingerprint text,
  user_agent         text,
  revoked_at         timestamptz,
  metadata           jsonb NOT NULL DEFAULT '{}'::jsonb,
  updated_at         timestamptz NOT NULL DEFAULT now(),
  created_by         varchar(26),
  updated_by         varchar(26),
  is_deleted         boolean NOT NULL DEFAULT false
);
CREATE INDEX IF NOT EXISTS user_session_user_idx ON user_session (user_id, last_seen_at DESC);
