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
