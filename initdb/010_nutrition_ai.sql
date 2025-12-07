-- 010_nutrition_ai.sql  (nutrients as JSONB; enum-free)

CREATE TABLE IF NOT EXISTS nutrition_daily_plan (
  id             varchar(26) PRIMARY KEY,
  user_id        varchar(26) NOT NULL,
  plan_date      date NOT NULL,
  ai_response_id varchar(26) REFERENCES ai_response(id),
  kcal_target    numeric(10,2),
  notes          text,
  status         varchar(24) NOT NULL DEFAULT 'READY',  -- READY | FALLBACK | INVALID
  created_at     timestamptz NOT NULL DEFAULT now(),
  updated_at     timestamptz NOT NULL DEFAULT now(),
  is_deleted     boolean NOT NULL DEFAULT false,
  UNIQUE (user_id, plan_date)
);

CREATE TABLE IF NOT EXISTS nutrition_plan_item (
  id             varchar(26) PRIMARY KEY,
  plan_id        varchar(26) NOT NULL REFERENCES nutrition_daily_plan(id),
  slot           varchar(24) NOT NULL,                 -- BREAKFAST|LUNCH|DINNER|SNACK_1|SNACK_2
  title          varchar(160) NOT NULL,
  description    varchar(500),
  nutrients      jsonb NOT NULL DEFAULT '{}'::jsonb,   -- flexible bag: {"kcal":420,"protein_g":14,"iron_mg":3.2,...}
  cuisine        varchar(48),
  allergens_hit  varchar(255),
  metadata       jsonb NOT NULL DEFAULT '{}'::jsonb
);

CREATE TABLE IF NOT EXISTS nutrition_feedback (
  id          varchar(26) PRIMARY KEY,
  plan_id     varchar(26) NOT NULL REFERENCES nutrition_daily_plan(id),
  user_id     varchar(26) NOT NULL,
  helpful     boolean NOT NULL,
  comments    varchar(500),
  created_at  timestamptz NOT NULL DEFAULT now(),
  metadata    jsonb NOT NULL DEFAULT '{}'::jsonb
);
