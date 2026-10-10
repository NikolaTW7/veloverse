-- One-off update (October 2026): replace the old sample brands and bicycle models.
--
-- Run this once on an existing VeloVerse database, with the application stopped:
--   psql -U postgres -d veloverse -f db/2026-10-replace-brands-and-models.sql
-- Then start the application. data.sql adds the five new brands and models on start-up.
--
-- New databases do not need this script: data.sql already contains only the new data.
--
-- Safety:
--   * Everything runs in one transaction, so either all of it happens or none of it does.
--   * The current rows are copied into backup tables first. They are only created the
--     first time, so running the script again never overwrites the original backup.
--   * Only bicycles reference brands, and nothing references bicycles, so deleting the
--     bicycles first and then the brands keeps every foreign key valid. Bicycle types,
--     materials and components are not touched.

BEGIN;

CREATE TABLE IF NOT EXISTS backup_2026_10_brands AS TABLE brands;
CREATE TABLE IF NOT EXISTS backup_2026_10_bicycles AS TABLE bicycles;

DELETE FROM bicycles;
DELETE FROM brands;

COMMIT;

-- To undo (restores the old brands and models exactly, with their original IDs):
--   BEGIN;
--   DELETE FROM bicycles;
--   DELETE FROM brands;
--   INSERT INTO brands (brand_id, name, country, description)
--       OVERRIDING SYSTEM VALUE
--       SELECT brand_id, name, country, description FROM backup_2026_10_brands;
--   INSERT INTO bicycles (bicycle_id, brand_id, type_id, frame_material_id, name, year, weight_kg,
--                         wheel_size, brake_system, drivetrain, gears, description, image_url)
--       OVERRIDING SYSTEM VALUE
--       SELECT bicycle_id, brand_id, type_id, frame_material_id, name, year, weight_kg,
--              wheel_size, brake_system, drivetrain, gears, description, image_url
--       FROM backup_2026_10_bicycles;
--   COMMIT;
-- Once you are happy with the new data, the backup tables can be removed with:
--   DROP TABLE backup_2026_10_bicycles, backup_2026_10_brands;
