-- One-off update (October 2026, second step): replace the five brands and five models from
-- 2026-10-replace-brands-and-models.sql with the approved collection of five brands
-- (Cervélo, Brompton, Colnago, Orbea, Canyon) and six models.
--
-- Run this once on an existing VeloVerse database, with the application stopped:
--   psql -U postgres -d veloverse -f db/2026-10b-approved-collection.sql
-- Then start the application. schema.sql makes the frame material optional, and data.sql
-- adds the new bicycle type, the five brands and the six models on start-up.
--
-- New databases do not need this script: data.sql already contains only the new data.
--
-- Safety:
--   * Everything runs in one transaction, so either all of it happens or none of it does.
--   * The current rows are copied into NEW backup tables (backup_2026_10b_...). They are only
--     created the first time, so running the script again never overwrites this backup.
--   * The earlier backup tables (backup_2026_10_brands and backup_2026_10_bicycles) are not
--     touched.
--   * Only bicycles reference brands, and nothing references bicycles, so deleting the
--     bicycles first and then the brands keeps every foreign key valid. Bicycle types,
--     materials and components are not touched.

BEGIN;

CREATE TABLE IF NOT EXISTS backup_2026_10b_brands AS TABLE brands;
CREATE TABLE IF NOT EXISTS backup_2026_10b_bicycles AS TABLE bicycles;

DELETE FROM bicycles;
DELETE FROM brands;

COMMIT;

-- To undo (restores the previous five brands and models exactly, with their original IDs).
-- Stop the application first. data.sql adds the new rows again on every start-up, so a
-- permanent undo also needs the previous version of data.sql:
--   BEGIN;
--   DELETE FROM bicycles;
--   DELETE FROM brands;
--   INSERT INTO brands OVERRIDING SYSTEM VALUE
--       SELECT * FROM backup_2026_10b_brands;
--   INSERT INTO bicycles OVERRIDING SYSTEM VALUE
--       SELECT * FROM backup_2026_10b_bicycles;
--   COMMIT;
-- (SELECT * works here because the backup tables were copied from the same tables,
-- with the same columns in the same order.)
-- Once you are happy with the new data, these backup tables can be removed with:
--   DROP TABLE backup_2026_10b_bicycles, backup_2026_10b_brands;
