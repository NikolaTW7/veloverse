# VeloVerse brand and model data: sources

Last checked: 10 October 2026

VeloVerse shows five brands and six bicycle models: Cervélo S5, a Brompton folding bicycle,
Colnago V3-RS, Orbea Avant, and two Canyon models (Speedmax CFR and Endurace WMN CF SL Disc 8.0 Di2)
under one Canyon brand. This file records where every fact came from and which values are
deliberately left empty. The data itself is in `src/main/resources/data.sql`. Photo credits and
licences are in `IMAGE-CREDITS.md`.

Rules followed:

- Each model was chosen together with a Wikimedia Commons photo, so the details describe the bicycle
  in that photo. Where the exact build, variant or year of the photographed bicycle could not be
  confirmed, the value is stored as `NULL` and shown as "Not specified". It is never guessed.
- The frame material is optional. When it cannot be confirmed (because the model was sold with
  different frame materials), it is stored as `NULL` and shown as "Not confirmed".
- Details that are only known from the photo or its description (for example the parts fitted to a
  shop's bike) are mentioned in the description text, not stored as specifications.
- Prices are quoted only inside the disadvantages text, with the year and place they come from.

## Updating an existing database

New databases need nothing extra: `data.sql` already contains only the new brands and models.

A database that still has the previous five brands (Cervélo, Giant, Tsunami, BXT, GRAY Cycling)
needs a one-off update. In PowerShell, from the project folder:

1. Stop the application.
2. Optional extra backup file:
   `pg_dump -U postgres -d veloverse --data-only --table=brands --table=bicycles -f brands-bicycles-backup-2.sql`
3. Run the update script:
   `psql -U postgres -d veloverse -f db/2026-10b-approved-collection.sql`
   It copies the current rows into the new tables `backup_2026_10b_brands` and
   `backup_2026_10b_bicycles`, then removes the previous brands and models in one transaction.
   The earlier backup tables `backup_2026_10_brands` and `backup_2026_10_bicycles` are not touched.
4. Start the application. `schema.sql` makes the frame material optional, and `data.sql` adds the
   Time-Trial / Triathlon Bike type, the five brands and the six models.

Restarting the application again does not create duplicates: brands and types are matched by name,
and models by brand and name (`ON CONFLICT ... DO NOTHING` plus a `NOT EXISTS` check, because a
`NULL` year is not caught by the unique key).

The bottom of the script explains how to undo the change from the backup tables.

The older script `db/2026-10-replace-brands-and-models.sql` belongs to the previous update and must
not be run again.

## Automated tests use their own database

`mvnw test` does not use the website's `veloverse` database. The test-only file
`src/test/resources/config/application.properties` points the tests at a separate PostgreSQL
database, `veloverse_test`, and `src/test/resources/test-reset.sql` empties that database before
`schema.sql` and `data.sql` rebuild it. So the tests always check exactly what `data.sql` creates,
whatever state the website's database is in. The reset script refuses to run on any database not
named `veloverse_test`.

Create the test database once: `createdb -U postgres veloverse_test`

## Database changes

| Change | Where | Purpose |
|---|---|---|
| `bicycles.frame_material_id` no longer `NOT NULL` | `schema.sql` (`ALTER TABLE ... DROP NOT NULL`, safe to repeat) | Frame material can be "Not confirmed" |
| New bicycle type "Time-Trial / Triathlon Bike" | `data.sql` | Type for the Canyon Speedmax CFR |

The optional columns added in the previous update (`brands.website`, `brands.specialities`,
`bicycles.intended_use`, `features`, `advantages`, `disadvantages`, `source_url`) are unchanged.

## Brands

| Brand | Country | Background sources |
|---|---|---|
| Cervélo | Canada (founded in Toronto, 1995; now owned by Pon Holdings, headquarters in California) | https://en.wikipedia.org/wiki/Cerv%C3%A9lo, https://www.cervelo.com |
| Brompton | United Kingdom (founded 1976 by Andrew Ritchie; made in Greenford, London; 16-inch wheels on most models, steel main frame with steel or titanium parts depending on the model; G Line with 20-inch wheels from late 2024) | https://en.wikipedia.org/wiki/Brompton_Bicycle, https://www.brompton.com |
| Colnago | Italy (founded 1954 by Ernesto Colnago in Cambiago near Milan; C40 carbon frame in the 1990s; Pogačar's 2020 Tour de France win; majority owned by Chimera Investments since 2020) | https://en.wikipedia.org/wiki/Colnago, https://www.colnago.com |
| Orbea | Spain (founded 1840 in Eibar as a gun maker; bicycles from 1930; workers' co-operative since 1969, part of Mondragon; headquarters in Mallabia) | https://en.wikipedia.org/wiki/Orbea, https://www.orbea.com |
| Canyon | Germany (Koblenz; grew from Radsport Arnold, founded 1985 by Roman and Franc Arnold; sells directly online) | https://en.wikipedia.org/wiki/Canyon_Bicycles, https://www.canyon.com |

## Models

### Cervélo S5 (2019)

- Source: https://bikerumor.com/2018/10/02/all-new-2019-cervelo-s5-slips-in-faster-sleekly-integrated-disc-only-aero-road-bike/
- Verified: full carbon frame and fork, disc brakes only (flat mount, 12 mm thru-axles), internal
  routing through bar and stem, adjustable two-piece bar and stem, 28 mm tyre clearance, 25 mm tyres
  on every build, five sizes 48–58 cm, ridden by Team Sunweb from 2019, complete bikes
  US$6,500–12,000.
- Photo: Commons describes it as a 2019 S5 with SRAM Red eTap and disc brakes. Cervélo sold the S5
  with four different builds, and the build was not confirmed independently, so drivetrain and
  gears are `NULL`. Weight is not published (`NULL`).

### Brompton folding bicycle (model not confirmed)

- Source: https://en.wikipedia.org/wiki/Brompton_Bicycle
- The Commons photo (2015) shows an unfolded orange Brompton, but its model line and year are not
  stated, so the name is simply "Folding Bicycle" and the year is `NULL`.
- Wheel size: 16-inch (349 mm rims). All Brompton models had this size until the 20-inch G Line
  was introduced in late 2024, so it applies to the bike photographed in 2015.
- Brake system: rim brakes are visible in the photo.
- Frame material: `NULL` ("Not confirmed"). Bromptons use a steel main frame, but some versions have
  titanium parts, and the photographed version is unknown.
- Not stored: drivetrain, gears, weight.

### Colnago V3-RS (year not confirmed)

- Source: https://www.bikeradar.com/news/colnago-v3rs/ (launch article, July 2019)
- Verified: monocoque carbon frame, rim and disc versions, about 30 mm tyre clearance, 790 g target
  frame weight (size 50, unpainted), dropped seatstays, hidden cables on the disc version, eight
  sizes, disc frameset £3,999.95 at launch. Colnago writes the name "V3Rs"; VeloVerse uses
  "V3-RS" as in the approved list and the Commons file name.
- Photo: disc brakes are visible; the Commons title mentions Shimano Dura-Ace Di2 and Princeton
  Carbon wheels. The year and full build are not confirmed, so year, drivetrain and gears are `NULL`.

### Orbea Avant (variant and year not confirmed)

- Source: https://road.cc/content/review/118552-orbea-avant-road-bike (review of the Avant M10D,
  May 2014)
- Verified for the Avant range of that time: endurance geometry with a higher stack and shorter
  reach than the Orca, carbon or aluminium frames, disc or rim brakes, room for 28 mm tyres, hidden
  mudguard eyelets, internal routing, tapered head tube.
- Photo (2015): the photographer describes the bike as "set up on Shimano Ultegra with their ...
  hydraulic disc" brakes. So the brake system is stored as hydraulic disc, and Ultegra is mentioned
  only in the description. Frame material and year are `NULL` because the variant is not confirmed.

### Canyon Speedmax CFR (2020)

- Sources: https://www.tour-magazin.de/en/canyon-speedmax-disc-2021-new-triathlon-bike-presented-by-canyon/
  (launch of the disc Speedmax range on 17 November 2020, prices €3,699–€11,999, bento box and
  tool box on the CF triathlon models) and https://99spokes.com/en-EU/bikes/canyon/2021/speedmax-cfr-disc-di2
  (carbon frame and fork, wheel size 650c or 700c depending on frame size).
- Year: 2020 is the date of the Commons photo and the year the disc Speedmax CFR was launched.
  Note that some retailers list the disc CFR as model year **2021**.
- Photo: disc brakes and aero extensions are visible. The groupset is not confirmed, so drivetrain
  and gears are `NULL`. Wheel size is `NULL` because it depends on the frame size.

### Canyon Endurace WMN CF SL Disc 8.0 Di2 (2020)

- Source: https://bikeinsights.com/bike-geometries/5ea197d562942c00179851e9-2020-canyon-bicycles-endurace-cf-sl-wmn-cf-sl-disc-80-di2-m
- Verified: model year 2020, carbon frame and fork, disc brakes, 700C wheels, 28 mm tyres,
  women's-specific fit. "Di2" in the model name means Shimano electronic shifting.
- Not confirmed: the exact groupset, number of gears and weight (`NULL`). An older 2018 spec sheet
  was found but not used, because it is a different year.
