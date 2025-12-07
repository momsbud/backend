-- 009_ai.sql  (enum-free; varchar only)

CREATE TABLE IF NOT EXISTS ai_prompt (
  id           varchar(26) PRIMARY KEY,
  user_id      varchar(26) NOT NULL,
  model        varchar(64) NOT NULL,          -- e.g., "LLAMA_8B_Q4"
  purpose      varchar(32) NOT NULL,          -- e.g., "NUTRITION_DAILY"
  prompt_text  text NOT NULL,
  created_at   timestamptz NOT NULL DEFAULT now(),
  metadata     jsonb NOT NULL DEFAULT '{}'::jsonb
);

CREATE TABLE IF NOT EXISTS ai_response (
  id             varchar(26) PRIMARY KEY,
  prompt_id      varchar(26) NOT NULL REFERENCES ai_prompt(id),
  status         varchar(24) NOT NULL,        -- OK | REPAIRED | INVALID | ERROR
  raw_text       text,                        -- full LLM output
  parsed_json    jsonb,                       -- parsed/normalized JSON
  tokens_prompt  int,
  tokens_output  int,
  cost_inr       numeric(10,2),               -- INR per your preference
  created_at     timestamptz NOT NULL DEFAULT now(),
  metadata       jsonb NOT NULL DEFAULT '{}'::jsonb
);
