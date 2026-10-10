-- VeloVerse starter content.
-- General educational descriptions of common bicycle types (no brand or model specifications).
-- Advantages and disadvantages hold one point per line (\n inside the E'...' strings).
-- ON CONFLICT DO NOTHING keeps this safe to re-run and never overwrites edited rows.

INSERT INTO bicycle_types (name, description, best_use, advantages, disadvantages) VALUES
('Road Bike',
 'Designed for efficient riding on paved roads. Road bikes are light, with drop handlebars, narrow tyres and a forward-leaning riding position that reduces wind resistance.',
 'Road cycling, fitness riding and speed on paved surfaces',
 E'Fast and efficient on smooth roads\nLight frames make climbing and accelerating easier\nDrop handlebars offer several hand positions and a lower, more aerodynamic posture',
 E'Not suited to rough trails, mud or loose gravel\nThe lower riding position can feel less comfortable for beginners\nNarrow tyres give less grip and cushioning on poor surfaces'),

('Mountain Bike',
 'Built for off-road riding on rough, uneven terrain. Mountain bikes use wide knobbly tyres, strong frames and usually front or full suspension to absorb bumps.',
 'Trails and off-road terrain',
 E'Strong grip and control on dirt, rocks and roots\nSuspension absorbs impacts for a more controlled ride\nWide gear range for steep climbs',
 E'Heavier and slower on paved roads\nKnobbly tyres create more rolling resistance\nSuspension adds cost and needs regular maintenance'),

('Gravel Bike',
 'A versatile drop-bar bike for both paved roads and unpaved tracks. Gravel bikes have wider tyres and more relaxed geometry than road bikes, and many have mounts for bags and racks.',
 'Mixed surfaces and adventure riding',
 E'Handles roads, gravel and light trails on one bike\nWider tyres add comfort and grip\nOften has mounts for racks, bags and bottles',
 E'Slower than a road bike on smooth tarmac\nLess capable than a mountain bike on technical trails'),

('Hybrid Bike',
 'Combines features of road and mountain bikes for everyday riding. Hybrids usually have flat handlebars, an upright riding position and medium-width tyres.',
 'Commuting and fitness riding',
 E'Comfortable upright position with good visibility in traffic\nFlat handlebars are easy to control\nPractical for commuting, often with mounts for racks and mudguards',
 E'Not as fast as a road bike over long distances\nNot designed for demanding off-road trails'),

('BMX',
 'Small, strong bikes, usually with 20-inch wheels, built for stunts, racing on dirt tracks and riding in skate parks. Most have a single gear.',
 'Tricks, jumps and BMX racing',
 E'Strong and durable for jumps and tricks\nCompact frame is easy to manoeuvre\nSimple design with few parts to maintain',
 E'Small size and a single gear make long rides tiring\nThe low seat is not comfortable for travelling distances'),

('Fixed-Gear Bike',
 'Single-speed bikes where the rear cog is fixed to the hub, so the pedals turn whenever the rear wheel turns. Riders can slow down by resisting the pedals.',
 'Urban riding and track cycling',
 E'Very simple, with few parts to maintain\nLight and efficient on flat ground\nDirect drive gives a strong feel for the bike''s speed',
 E'No coasting: the pedals keep turning while the bike moves\nA single gear makes hills difficult\nTakes practice to ride and stop confidently'),

('Touring Bike',
 'Built for long-distance travel while carrying luggage. Touring bikes have strong frames, stable handling, a wide gear range and mounts for racks and mudguards.',
 'Long-distance travel with luggage',
 E'Stable and comfortable when carrying heavy loads\nDurable frames and wheels\nWide gear range for loaded climbs',
 E'Heavier than road or gravel bikes\nFeels less responsive when ridden without luggage'),

('Electric Bike',
 'Bicycles with a battery-powered motor that assists the rider while pedalling. Electric bikes come in many styles, including city, mountain and cargo designs.',
 'Assisted riding, commuting and hills',
 E'Makes hills and longer distances easier\nUseful for commuting without arriving tired\nAvailable in many different bicycle styles',
 E'Heavier and more expensive than similar non-electric bikes\nThe battery must be charged and eventually replaced\nRules on speed and motor power vary between countries')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------
-- Materials (general properties, one advantage/disadvantage per line)
-- ---------------------------------------------------------------
INSERT INTO materials (name, description, advantages, disadvantages) VALUES
('Aluminium',
 'A light metal alloy and the most common material for frames and parts on entry-level and mid-range bicycles.',
 E'Light and stiff for its cost\nDoes not rust like steel\nEasy to shape into many tube profiles',
 E'Can give a harsher ride than steel or carbon fibre\nLess resistant to fatigue over very long periods of use'),
('Carbon Fibre',
 'A composite made from layers of carbon fibre sheets bonded with resin. Engineers can arrange the layers to make a part stiff in one direction and more flexible in another.',
 E'Very high strength and stiffness for its weight\nCan be tuned for both stiffness and comfort\nAllows aerodynamic and complex shapes',
 E'Expensive to make\nCan be damaged by sharp impacts or over-tightened bolts\nDamage is not always visible from the outside'),
('Steel',
 'A strong and durable iron-based alloy. It is used for frames as well as for parts that wear quickly, such as chains, cassette sprockets and brake rotors.',
 E'Very durable and resistant to wear\nComfortable ride quality when used for frames\nEasy to repair and relatively inexpensive',
 E'Heavier than aluminium, carbon fibre or titanium\nCan rust if not protected'),
('Titanium',
 'A strong, light and corrosion-resistant metal used for premium frames and some high-end small parts.',
 E'Does not rust or corrode\nStrong, durable and resistant to fatigue\nComfortable ride quality',
 E'Expensive to buy and difficult to machine and weld\nUsually heavier than a carbon fibre equivalent'),
('Rubber',
 'A flexible, grippy material used for tyres. Different rubber compounds balance grip, rolling resistance and wear.',
 E'Provides grip on the road or trail\nAbsorbs small bumps and vibration',
 E'Wears down with use and needs replacing\nCan be cut or punctured by sharp objects')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------
-- Component systems
-- ---------------------------------------------------------------
INSERT INTO component_systems (name, description) VALUES
('Frame System', 'The backbone of the bicycle. It provides the structure, holds every other part in place and defines the riding position.'),
('Drivetrain', 'The system that converts the rider''s pedalling into forward motion by transferring power to the rear wheel.'),
('Braking System', 'The parts that let the rider control speed and stop safely.'),
('Wheel System', 'The parts that roll along the ground. They affect grip, comfort and how efficiently the bicycle moves.'),
('Cockpit / Steering', 'The parts the rider holds to steer, control the bicycle and support the upper body.'),
('Seating', 'The parts that support the rider''s weight and set the saddle height and position.')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------
-- Components (sort_order keeps the IDs in a sensible display order)
-- ---------------------------------------------------------------
INSERT INTO components (system_id, name, description, purpose, operation)
SELECT s.system_id, v.name, v.description, v.purpose, v.operation
FROM (VALUES
 (1, 'Frame System', 'Frame',
  'The main structure of the bicycle, made of joined tubes. Every other component is attached to it.',
  'Carries the rider''s weight, holds the wheels and components in the correct positions and sets the bicycle''s geometry.',
  'The tubes form a strong triangular shape. The angles and lengths of the tubes decide how the bicycle handles and how the rider sits.'),
 (2, 'Frame System', 'Fork',
  'The two-legged part that holds the front wheel and connects it to the frame through the head tube.',
  'Lets the rider steer by turning the front wheel. Suspension forks also absorb bumps.',
  'The steerer tube at the top of the fork turns inside bearings in the frame''s head tube. The handlebar and stem turn the steerer, which turns the front wheel.'),
 (3, 'Drivetrain', 'Crankset',
  'The crank arms and the toothed chainrings attached to them. The pedals screw into the ends of the crank arms.',
  'Turns the rider''s pedalling force into rotation that drives the chain.',
  'Pushing the pedals rotates the crank arms around the bottom bracket. The chainrings turn with them and pull the chain forward.'),
 (4, 'Drivetrain', 'Chain',
  'A loop of linked metal plates and rollers that connects the chainring at the front to the cassette at the back.',
  'Transfers power from the crankset to the rear wheel.',
  'The rollers sit between the teeth of the chainring and the rear sprockets. As the chainring turns it pulls the chain, which turns the rear sprocket and wheel.'),
 (5, 'Drivetrain', 'Cassette',
  'A cluster of several sprockets of different sizes mounted on the rear wheel hub.',
  'Gives the rider a range of gears, so pedalling can stay comfortable on climbs and at higher speeds.',
  'The derailleur moves the chain between sprockets. A larger sprocket makes pedalling easier but slower, and a smaller sprocket makes it harder but faster.'),
 (6, 'Drivetrain', 'Rear Derailleur',
  'A sprung mechanism mounted near the rear wheel that guides the chain.',
  'Moves the chain from one cassette sprocket to another to change gear and keeps the chain tight.',
  'When the rider uses the shifter, a cable or electronic signal moves the derailleur sideways and the chain follows onto the next sprocket. A spring-loaded arm takes up the slack in the chain.'),
 (7, 'Braking System', 'Brake Lever',
  'The lever on the handlebar that the rider pulls to brake.',
  'Lets the rider control how hard the brakes are applied.',
  'Pulling the lever either pulls a cable or pushes hydraulic fluid through a hose, which makes the brake caliper squeeze the brake pads.'),
 (8, 'Braking System', 'Brake Caliper',
  'The part that holds the brake pads next to the rim or the brake rotor.',
  'Presses the brake pads against the braking surface to slow the wheel.',
  'Force from the brake lever moves the caliper''s arms or pistons, pressing the pads onto the rim or rotor. Friction turns the bicycle''s movement into heat.'),
 (9, 'Braking System', 'Brake Rotor',
  'A metal disc bolted to the wheel hub on bicycles with disc brakes.',
  'Provides the surface that the disc brake pads grip to slow the wheel.',
  'The rotor turns with the wheel. When the caliper squeezes the pads against it, friction slows the rotor and therefore the wheel. Rotors usually work more consistently in wet conditions than rim brakes.'),
 (10, 'Wheel System', 'Rim',
  'The outer metal or carbon hoop of the wheel that the tyre is mounted on.',
  'Holds the tyre in shape and gives the wheel its strength. On rim-brake bicycles it is also the braking surface.',
  'The spokes pull the rim evenly towards the hub, keeping it round and true. The tyre bead hooks into the edges of the rim.'),
 (11, 'Wheel System', 'Hub & Spokes',
  'The hub is the centre of the wheel and contains the bearings. The spokes are thin metal rods connecting the hub to the rim.',
  'Lets the wheel spin smoothly around the axle and transfers the rider''s weight and pedalling force to the rim.',
  'Bearings inside the hub allow it to rotate freely. Each spoke is kept under tension, and together they spread loads around the whole wheel.'),
 (12, 'Wheel System', 'Tyre',
  'The rubber outer layer of the wheel that touches the ground.',
  'Provides grip, absorbs small bumps and affects how easily the bicycle rolls.',
  'Air pressure inside the tyre supports the rider''s weight. Wide, knobbly tyres grip loose ground, while narrow, smooth tyres roll easily on roads.'),
 (13, 'Cockpit / Steering', 'Handlebar',
  'The bar that the rider holds to steer. Common shapes include drop bars and flat bars.',
  'Lets the rider steer, holds the brake levers and shifters, and supports the upper body.',
  'Turning the handlebar rotates the stem and fork steerer, which turns the front wheel. The bar''s shape decides the hand positions available to the rider.'),
 (14, 'Cockpit / Steering', 'Stem',
  'The short part that clamps the handlebar to the fork''s steerer tube.',
  'Connects the handlebar to the fork and sets how far forward and how high the handlebar sits.',
  'The stem clamps tightly around the steerer tube, so turning the handlebar turns the fork. A longer or lower stem stretches the rider into a more forward position.'),
 (15, 'Seating', 'Saddle',
  'The seat of the bicycle, mounted on top of the seatpost.',
  'Supports the rider''s weight while pedalling.',
  'The saddle''s rails are clamped to the seatpost, which allows its angle and its forward or backward position to be adjusted.'),
 (16, 'Seating', 'Seatpost',
  'The tube that holds the saddle and slides into the frame''s seat tube.',
  'Sets the saddle height, which affects pedalling efficiency and comfort.',
  'The seatpost is held in the seat tube by a clamp. Loosening the clamp lets the rider raise or lower the saddle.')
) AS v(sort_order, system_name, name, description, purpose, operation)
JOIN component_systems s ON s.name = v.system_name
ORDER BY v.sort_order
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------
-- Common materials for each component
-- ---------------------------------------------------------------
INSERT INTO component_materials (component_id, material_id)
SELECT c.component_id, m.material_id
FROM (VALUES
 ('Frame', 'Aluminium'), ('Frame', 'Carbon Fibre'), ('Frame', 'Steel'), ('Frame', 'Titanium'),
 ('Fork', 'Aluminium'), ('Fork', 'Carbon Fibre'), ('Fork', 'Steel'),
 ('Crankset', 'Aluminium'), ('Crankset', 'Carbon Fibre'), ('Crankset', 'Steel'),
 ('Chain', 'Steel'),
 ('Cassette', 'Steel'), ('Cassette', 'Aluminium'),
 ('Rear Derailleur', 'Aluminium'), ('Rear Derailleur', 'Steel'),
 ('Brake Lever', 'Aluminium'), ('Brake Lever', 'Carbon Fibre'),
 ('Brake Caliper', 'Aluminium'),
 ('Brake Rotor', 'Steel'),
 ('Rim', 'Aluminium'), ('Rim', 'Carbon Fibre'),
 ('Hub & Spokes', 'Aluminium'), ('Hub & Spokes', 'Steel'),
 ('Tyre', 'Rubber'),
 ('Handlebar', 'Aluminium'), ('Handlebar', 'Carbon Fibre'), ('Handlebar', 'Steel'),
 ('Stem', 'Aluminium'), ('Stem', 'Carbon Fibre'),
 ('Saddle', 'Steel'), ('Saddle', 'Titanium'), ('Saddle', 'Carbon Fibre'),
 ('Seatpost', 'Aluminium'), ('Seatpost', 'Carbon Fibre')
) AS v(component_name, material_name)
JOIN components c ON c.name = v.component_name
JOIN materials m ON m.name = v.material_name
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------
-- Brands: five manufacturers with six bicycle models below (Canyon has two).
-- Sources for every fact are listed in DATA-SOURCES.md.
-- ---------------------------------------------------------------
INSERT INTO brands (name, country, description, website, specialities) VALUES
('Cervélo', 'Canada',
 'A bicycle company founded in 1995 in Toronto, Canada, by engineers Gérard Vroomen and Phil White. Cervélo is best known for aerodynamic road and triathlon bikes ridden by professional racing teams. It is now owned by the Dutch company Pon Holdings and has its headquarters in California, United States.',
 'https://www.cervelo.com',
 E'Aerodynamic road bikes (S-Series)\nTriathlon and time-trial bikes (P-Series)\nLightweight climbing road bikes (R-Series)\nGravel bikes (Áspero)'),
('Brompton', 'United Kingdom',
 'A British company founded in 1976 by Andrew Ritchie and known for its folding bicycles, which fold into a compact package that stands up on its own. Its bikes are made in Greenford, London. Most models use 16-inch wheels and a steel main frame, and some versions have titanium parts.',
 'https://www.brompton.com',
 E'Compact folding bikes with 16-inch wheels\nFolding bikes with 20-inch wheels (G Line, from 2024)\nElectric folding bikes'),
('Colnago', 'Italy',
 'An Italian company founded in 1954 by Ernesto Colnago in Cambiago, near Milan. Colnago became famous for high-quality steel racing frames and later for carbon-fibre race bikes, starting with the C40 in the 1990s. Tadej Pogačar won the 2020 Tour de France on a Colnago. Since 2020 the company has been majority-owned by the UAE-based Chimera Investments.',
 'https://www.colnago.com',
 E'Road racing bikes\nCarbon-fibre frames made in Italy (C-series, e.g. C68)\nMonocoque carbon race frames (V-series, e.g. V3Rs)'),
('Orbea', 'Spain',
 'A Spanish company from the Basque Country, founded in 1840 in Eibar as a maker of guns. Orbea began making bicycles in 1930, and since 1969 it has been a workers'' co-operative and part of the Mondragon Corporation. Its headquarters are in Mallabia.',
 'https://www.orbea.com',
 E'Road bikes (e.g. Orca and Avant)\nMountain bikes\nTriathlon bikes\nUrban and electric bikes'),
('Canyon', 'Germany',
 'A German company based in Koblenz. It grew from Radsport Arnold, a bike parts business started in 1985 by brothers Roman and Franc Arnold, and has used the Canyon Bicycles name since the early 2000s. Canyon sells its bikes directly to riders online instead of through bike shops.',
 'https://www.canyon.com',
 E'Road bikes (e.g. Ultimate and Endurace)\nTriathlon and time-trial bikes (Speedmax)\nMountain bikes\nElectric bikes')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------
-- Ninth bicycle type
-- ---------------------------------------------------------------
INSERT INTO bicycle_types (name, description, best_use, advantages, disadvantages) VALUES
('Folding Bike',
 'Compact bikes, usually with small wheels, whose frame folds so the bike can be carried onto public transport or stored in a small space.',
 'Commuting combined with trains or buses, and storage in small homes',
 E'Folds small enough to carry onto trains and buses\nEasy to store indoors at home or at work\nPractical for journeys that combine cycling with public transport',
 E'Small wheels feel less smooth on rough surfaces\nOften heavier and more expensive than a non-folding bike of similar quality\nLess suited to long distances or high speeds')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------
-- Tenth bicycle type (added with the Canyon Speedmax CFR)
-- ---------------------------------------------------------------
INSERT INTO bicycle_types (name, description, best_use, advantages, disadvantages) VALUES
('Time-Trial / Triathlon Bike',
 'Road bikes built for riding alone against the clock in time trials and triathlons. The rider leans forward on aero extensions fixed to the handlebar, and the frame, wheels and parts are shaped to reduce air resistance, which is the biggest force a cyclist works against at high speed.',
 'Time trials, triathlons and fast solo riding on smooth, open roads',
 E'Low, narrow riding position and aerodynamic shapes save a lot of energy at high speed\nAero extensions let the rider rest on the forearms while keeping that position\nMany triathlon models have built-in storage for drinks, food and tools',
 E'The brake levers are on the base bar, away from the aero extensions, so the bike is less agile and slower to stop\nNot suited to group rides or mass-start races, where riders need quick access to the brakes\nThe stretched position needs a careful bike fit and takes time to get used to\nTime-trial races run under UCI rules only allow approved bikes, so not every triathlon bike can be used there')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------
-- Bicycle models: six models from five brands (Canyon has two).
-- Values that could not be verified are left NULL and shown as "Not specified"
-- ("Not confirmed" for the frame material) rather than guessed.
-- Brand, type and material are looked up by name, so no ID numbers are hard-coded.
-- Features, advantages and disadvantages store one item per line.
-- ---------------------------------------------------------------
INSERT INTO bicycles (brand_id, type_id, frame_material_id, name, year, weight_kg,
                      wheel_size, brake_system, drivetrain, gears, description,
                      intended_use, features, advantages, disadvantages, source_url)
SELECT b.brand_id, t.type_id, m.material_id, v.name, v.year, v.weight_kg,
       v.wheel_size, v.brake_system, v.drivetrain, v.gears, v.description,
       v.intended_use, v.features, v.advantages, v.disadvantages, v.source_url
FROM (VALUES
 (1, 'Cervélo', 'Road Bike', 'Carbon Fibre', 'S5', 2019, NULL::NUMERIC,
  '700c', 'Disc', NULL, NULL::INTEGER,
  'Cervélo''s aerodynamic road race bike. These details are for the 2019 S5, the first S5 to be made only with disc brakes. It has a full carbon frame and fork, and it was used by the professional Sunweb team from 2019. The S5 was sold in several builds with different groupsets and wheels, and the exact build in the photo is not confirmed, so the groupset is not listed here.',
  'Road racing and fast rides on smooth roads, where cutting through the air saves the most energy.',
  E'Full carbon aerodynamic frame with a carbon fork\nDisc brakes only, with 12 mm thru-axles\nCables routed inside the handlebar and stem\nRoom for tyres up to 28 mm (25 mm tyres were fitted on every build)\nFive frame sizes from 48 to 58 cm',
  E'Very aerodynamic, so it stays fast at high speeds\nDisc brakes work consistently, also in wet weather\nAdjustable bar and stem for fitting the riding position',
  E'Expensive: complete bikes cost from US$6,500 to US$12,000 at launch in 2018\nThe low race position can feel uncomfortable on long, relaxed rides\nTyres are limited to 28 mm, so it is not meant for rough roads or gravel',
  'https://bikerumor.com/2018/10/02/all-new-2019-cervelo-s5-slips-in-faster-sleekly-integrated-disc-only-aero-road-bike/'),
 (2, 'Brompton', 'Folding Bike', NULL, 'Folding Bicycle', NULL, NULL,
  '16-inch (349 mm rim)', 'Rim brakes', NULL, NULL,
  'A Brompton folding bicycle, shown unfolded. Brompton sells several model lines and the exact model and year of the bike in the photo are not confirmed, so only details shared by its 16-inch-wheel folding bikes are listed. Bromptons use a steel main frame, but on some models parts of the frame are titanium, so the frame material is shown as not confirmed.',
  'Commuting that combines cycling with trains or buses, and riding in town when storage space is small.',
  E'Folds into a compact package that stands up on its own\n16-inch wheels (349 mm rims)\nHinged main frame and handlebar stem\nRear frame section folds underneath the bike\nMudguards fitted on the bike in the photo',
  E'Small enough when folded to take on public transport or keep under a desk\nQuick to fold and unfold\nSmall wheels make it easy to manoeuvre in town',
  E'Small wheels feel less smooth on rough roads and potholes\nNot designed for long, fast rides\nMore expensive than many non-folding bikes',
  'https://en.wikipedia.org/wiki/Brompton_Bicycle'),
 (3, 'Colnago', 'Road Bike', 'Carbon Fibre', 'V3-RS', NULL, NULL,
  '700c', 'Disc', NULL, NULL,
  'Colnago''s monocoque carbon race bike, written "V3Rs" by Colnago and launched in July 2019. It was sold with rim brakes or disc brakes, and the bike in the photo has disc brakes. Colnago gave a target frame weight of 790 g (size 50, unpainted). The model year and full build of the bike in the photo are not confirmed, so they are not listed here.',
  'Road racing and fast all-round riding on paved roads, including hilly routes.',
  E'Monocoque carbon-fibre frame\nDropped seatstays and a hidden seatpost clamp\nAvailable with rim brakes or disc brakes\nCables hidden inside the stem and steerer on the disc version\nEight frame sizes',
  E'Light frame for climbing and quick acceleration\nStiffer than the previous V2-R, according to Colnago\nDisc version gives consistent braking in all weather',
  E'Expensive: the frameset alone cost £3,999.95 (disc) in the UK at launch\nAn aggressive race position that may not suit beginners\nTyre clearance is about 30 mm, so it is not made for gravel',
  'https://www.bikeradar.com/news/colnago-v3rs/'),
 (4, 'Orbea', 'Road Bike', NULL, 'Avant', NULL, NULL,
  '700c', 'Hydraulic disc', NULL, NULL,
  'Orbea''s endurance road bike, designed for comfort on long rides with a more upright position than Orbea''s Orca race bike. The Avant was sold with a carbon or an aluminium frame and with disc or rim brakes. The variant and year of the bike in the photo are not confirmed: the photographer describes it as set up with Shimano Ultegra and hydraulic disc brakes.',
  'Long road rides, sportives and comfortable training rides on paved roads.',
  E'Endurance geometry: higher handlebar and shorter reach than a race bike\nRoom for tyres up to 28 mm\nHidden mudguard mounts\nInternal cable routing and a tapered head tube\nShimano Ultegra parts and hydraulic disc brakes on the bike in the photo',
  E'More upright and comfortable than a race bike on long rides\nRoom for wider tyres and mudguards makes it practical all year\nHydraulic disc brakes give strong braking in the wet',
  E'Less aerodynamic and a little slower than a pure race bike\nTyres are limited to about 28 mm, so it is not a gravel bike\nThe exact frame material depends on the version',
  'https://road.cc/content/review/118552-orbea-avant-road-bike'),
 (5, 'Canyon', 'Time-Trial / Triathlon Bike', 'Carbon Fibre', 'Speedmax CFR', 2020, NULL,
  NULL, 'Disc', NULL, NULL,
  'Canyon''s top-level carbon triathlon bike. The disc-brake Speedmax range was launched in November 2020, with the CFR as its most expensive version, and this studio photo from 2020 shows a Speedmax CFR with disc brakes and aero extensions. The wheel size depends on the frame size (650c or 700c) and the exact build is not confirmed, so these are not listed here.',
  'Triathlons and solo riding at high speed on smooth roads, where a low, aerodynamic position saves the most energy.',
  E'Canyon CFR carbon frame and carbon fork\nDisc brakes\nAero extensions for a low, narrow riding position\nBuilt-in storage on the triathlon Speedmax Disc models: a bento box for food and drink and a tool box\nWheel size depends on frame size (650c or 700c)',
  E'Very aerodynamic, so it is fast on flat and rolling roads\nThe storage built into the frame keeps food and tools tidy and out of the wind\nDisc brakes work consistently, also in wet weather',
  E'Very expensive: the top Speedmax CFR cost €11,999 at its launch in November 2020\nThe aero position is tiring for beginners and needs a careful bike fit\nNot suited to group rides or hilly, technical roads',
  'https://www.tour-magazin.de/en/canyon-speedmax-disc-2021-new-triathlon-bike-presented-by-canyon/'),
 (6, 'Canyon', 'Road Bike', 'Carbon Fibre', 'Endurace WMN CF SL Disc 8.0 Di2', 2020, NULL,
  '700c', 'Disc', 'Shimano Di2 electronic shifting', NULL,
  'A carbon endurance road bike from Canyon''s 2020 range. "WMN" was Canyon''s name for its versions with women''s-specific fit, and "Di2" means Shimano electronic gear shifting. These details are for the 2020 model; the exact groupset, gear count and weight were not confirmed, so they are not listed here.',
  'Long road rides and fitness riding on paved roads, for riders who want comfort over many hours.',
  E'Carbon frame and carbon fork\nDisc brakes\n700C wheels with 28 mm tyres\nShimano Di2 electronic gear shifting\nWomen''s-specific fit (WMN)',
  E'Comfortable endurance position for long rides\nElectronic gears change quickly and precisely at the press of a button\n28 mm tyres add comfort and grip',
  E'Electronic shifting needs its battery charged\nSold mainly online, so test rides and shop support may be harder to arrange\nLess aerodynamic than a race bike such as the Speedmax or an aero road bike',
  'https://bikeinsights.com/bike-geometries/5ea197d562942c00179851e9-2020-canyon-bicycles-endurace-cf-sl-wmn-cf-sl-disc-80-di2-m')
) AS v(sort_order, brand_name, type_name, material_name, name, year, weight_kg,
       wheel_size, brake_system, drivetrain, gears, description,
       intended_use, features, advantages, disadvantages, source_url)
JOIN brands b ON b.name = v.brand_name
JOIN bicycle_types t ON t.name = v.type_name
-- LEFT JOIN: a model whose frame material is not confirmed keeps a NULL material
LEFT JOIN materials m ON m.name = v.material_name
-- The unique key treats a NULL year as different every time, so check by brand and name as well
WHERE NOT EXISTS (SELECT 1 FROM bicycles x WHERE x.brand_id = b.brand_id AND x.name = v.name)
ORDER BY v.sort_order
ON CONFLICT (brand_id, name, year) DO NOTHING;
