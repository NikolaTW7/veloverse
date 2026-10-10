# Database Design - BikePedia

## 1. Purpose

BikePedia needs a database to keep the bicycle information organised and connected.

Instead of writing the same brand, bicycle type, material or component information many times, I separate the information into related tables. This makes the data easier to manage and lets the application use the same information across different pages.

The database supports the main Version 1 features:

- browsing bicycle types
- viewing bicycle components and how they work
- browsing selected brands and bicycle models
- searching stored bicycle information
- filtering bicycles by type, brand and material
- comparing selected bicycles

## 2. Database Approach

I chose a relational database because the information in BikePedia has clear relationships.

For example, one brand can have many bicycle models, one bicycle type can contain many bicycles, and one material can be used by many bicycles or components.

The Version 1 database contains seven tables:

1. `brands`
2. `bicycle_types`
3. `materials`
4. `bicycles`
5. `component_systems`
6. `components`
7. `component_materials`

## 3. Tables

### 3.1 brands

Stores information about bicycle manufacturers.

| Field | Type | Purpose |
|---|---|---|
| `brand_id` | integer, PK | Unique ID for each brand |
| `name` | varchar | Brand name |
| `country` | varchar | Country associated with the brand |
| `description` | text | Short information about the brand |

### 3.2 bicycle_types

Stores the main categories of bicycles.

| Field | Type | Purpose |
|---|---|---|
| `type_id` | integer, PK | Unique ID for each bicycle type |
| `name` | varchar | Type name |
| `description` | text | Description of the bicycle type |
| `best_use` | text | Common use of the bicycle type |
| `advantages` | text | Main advantages |
| `disadvantages` | text | Main disadvantages |

### 3.3 materials

Stores information about materials used in bicycles and components.

| Field | Type | Purpose |
|---|---|---|
| `material_id` | integer, PK | Unique ID for each material |
| `name` | varchar | Material name |
| `description` | text | Description of the material |
| `advantages` | text | Main advantages |
| `disadvantages` | text | Main disadvantages |

### 3.4 bicycles

Stores the selected bicycle models shown in BikePedia.

| Field | Type | Purpose |
|---|---|---|
| `bicycle_id` | integer, PK | Unique ID for each bicycle |
| `brand_id` | integer, FK | Connects the bicycle to its brand |
| `type_id` | integer, FK | Connects the bicycle to its bicycle type |
| `frame_material_id` | integer, FK | Connects the bicycle to its frame material |
| `name` | varchar | Bicycle model name |
| `year` | integer | Model year |
| `weight_kg` | decimal | Bicycle weight in kilograms |
| `wheel_size` | varchar | Wheel size |
| `brake_system` | varchar | Braking system |
| `drivetrain` | varchar | Drivetrain information |
| `gears` | integer | Number of gears |
| `description` | text | Short information about the bicycle |
| `image_url` | varchar | Reference to the bicycle image |

The comparison fields such as weight, wheel size, brake system, drivetrain and gears are stored here because they can be displayed side-by-side on the bicycle comparison page.

### 3.5 component_systems

Groups bicycle components into larger systems.

Examples include drivetrain, braking, wheel, cockpit/steering and seating.

| Field | Type | Purpose |
|---|---|---|
| `system_id` | integer, PK | Unique ID for each component system |
| `name` | varchar | System name |
| `description` | text | Description of the system |

### 3.6 components

Stores information about individual bicycle parts.

| Field | Type | Purpose |
|---|---|---|
| `component_id` | integer, PK | Unique ID for each component |
| `system_id` | integer, FK | Connects the component to its system |
| `name` | varchar | Component name |
| `description` | text | What the component is |
| `purpose` | text | What the component does |
| `operation` | text | How the component works |
| `image_url` | varchar | Reference to the component image |

### 3.7 component_materials

Connects components and materials.

| Field | Type | Purpose |
|---|---|---|
| `component_id` | integer, PK/FK | References a component |
| `material_id` | integer, PK/FK | References a material |

I use this linking table because a component can commonly be made from more than one material, and the same material can be used by many different components.

## 4. Relationships

| Relationship | Type | Description |
|---|---|---|
| Brand -> Bicycle | One-to-Many | One brand can have many bicycle models |
| Bicycle Type -> Bicycle | One-to-Many | One bicycle type can have many bicycles |
| Material -> Bicycle | One-to-Many | One material can be used as the frame material for many bicycles |
| Component System -> Component | One-to-Many | One system can contain many components |
| Component <-> Material | Many-to-Many | Components can use several materials and materials can be used by several components |

The many-to-many relationship between components and materials is handled by the `component_materials` table.

## 5. Primary Keys and Foreign Keys

A primary key (PK) uniquely identifies a record in a table. For example, `bicycle_id` identifies one bicycle.

A foreign key (FK) connects one table to another. For example, `brand_id` inside the `bicycles` table connects a bicycle to the correct record in the `brands` table.

The main foreign keys are:

- `bicycles.brand_id` -> `brands.brand_id`
- `bicycles.type_id` -> `bicycle_types.type_id`
- `bicycles.frame_material_id` -> `materials.material_id`
- `components.system_id` -> `component_systems.system_id`
- `component_materials.component_id` -> `components.component_id`
- `component_materials.material_id` -> `materials.material_id`

## 6. ER Diagram

The ER diagram for this design is stored separately as:

`ER-diagram.png`

It shows the seven tables, their keys and the relationships between them.

## 7. Version 1 Scope

The database is for the selected educational content used by BikePedia. It is not intended to store every bicycle model in the world.

Version 1 does not contain tables for:

- user accounts
- administrator accounts
- orders
- payments
- shopping carts
- retailer inventory
- delivery information

These are not required for the current educational purpose of the application.

## 8. Future Changes

The database can be extended later if the project grows. For example, the planned 3D bicycle explorer could connect interactive 3D parts to the existing `components` records.

For Version 1, I will keep the database focused on the information required for browsing, learning, searching, filtering and bicycle comparison.
