-- Used only by the automated tests (see config/application.properties in this folder).
-- Removes the VeloVerse tables from the TEST database so that schema.sql and data.sql
-- rebuild them from scratch for every test run.

-- Safety check: if this is not the veloverse_test database, the next statement fails with
-- an error such as: invalid input syntax for type integer: "veloverse is not veloverse_test ..."
-- and the script stops, so nothing is dropped.
-- How it works: the WHERE makes the SELECT return a row only in the wrong database, and
-- turning that sentence into an INTEGER with CAST is impossible, so PostgreSQL reports an error.
-- In veloverse_test the WHERE matches nothing, so the CAST never runs and there is no error.
SELECT CAST(current_database() || ' is not veloverse_test: refusing to reset it' AS INTEGER)
WHERE current_database() <> 'veloverse_test';

-- DROP TABLE IF EXISTS deletes the tables (structure and rows) and does not fail if a table is missing.
-- CASCADE also removes anything that depends on these tables, such as foreign keys pointing to them.
DROP TABLE IF EXISTS bicycles, brands, component_materials, components, component_systems,
    materials, bicycle_types CASCADE;
