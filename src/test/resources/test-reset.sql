-- Used only by the automated tests (see config/application.properties in this folder).
-- Removes the VeloVerse tables from the TEST database so that schema.sql and data.sql
-- rebuild them from scratch for every test run.

-- Safety check: if this is not the veloverse_test database, the next statement fails with
-- an error such as: invalid input syntax for type integer: "veloverse is not veloverse_test ..."
-- and the script stops, so nothing is dropped.
SELECT CAST(current_database() || ' is not veloverse_test: refusing to reset it' AS INTEGER)
WHERE current_database() <> 'veloverse_test';

DROP TABLE IF EXISTS bicycles, brands, component_materials, components, component_systems,
    materials, bicycle_types CASCADE;
