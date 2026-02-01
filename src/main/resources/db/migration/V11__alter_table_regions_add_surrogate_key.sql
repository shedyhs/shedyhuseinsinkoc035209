-- Add surrogate primary key and rename id to external_id
-- This allows multiple rows with the same external_id (active + inactive history)

ALTER TABLE regions DROP CONSTRAINT regions_pkey;

ALTER TABLE regions RENAME COLUMN id TO external_id;

ALTER TABLE regions ADD COLUMN id SERIAL PRIMARY KEY;
