<p align="center">
  <img src="docs/images/logo-veloverse.jpg" alt="VeloVerse Logo" width="420">
</p>

# VeloVerse

### Explore. Understand. Compare.

VeloVerse is an interactive bicycle education platform designed to help beginners and cycling enthusiasts learn about bicycles, explore their components and materials, discover different models, and compare bicycles side by side.

## Features

- Nine bicycle types with educational information
- Interactive bicycle anatomy diagram
- Detailed bicycle component explanations
- Bicycle materials and their characteristics
- Bicycle brands and models
- Search and filtering
- Side-by-side bicycle comparison
- Responsive dark-themed interface

## Website Screenshots

### Homepage
![VeloVerse Homepage](docs/screenshots/01_Homepage.png)

### Bicycle Types
![Bicycle Types](docs/screenshots/02_Bicycle_Types.png)

### Interactive Bicycle Anatomy
![Interactive Bicycle Anatomy](docs/screenshots/03_Interactive_Anatomy.png)

### Bicycle Components
![Bicycle Components](docs/screenshots/04_Components.png)

### Bicycle Materials
![Bicycle Materials](docs/screenshots/05_Materials.png)

### Bicycle Brands
![Bicycle Brands](docs/screenshots/06_Brands.png)

### Bicycle Comparison
![Comparison Selection](docs/screenshots/07_Compare_Selection.png)

![Comparison Results](docs/screenshots/08_Compare_Results.png)

### Bicycle Details
![Bicycle Details](docs/screenshots/09_Bicycle_Details.png)

## Technology Stack

| Category | Technologies |
|---|---|
| Backend | Java 21, Spring Boot |
| Frontend | HTML, CSS, JavaScript, Thymeleaf |
| Database | PostgreSQL |
| Data Access | Spring Data JPA |
| Build Tool | Maven |
| Testing | JUnit, Maven |
| Version Control | Git and GitHub |

## Testing

The project includes automated tests covering application functionality.

Latest local test results:

- 97 tests executed
- 97 passed
- 0 failures
- 0 errors
- 0 skipped

To run the tests on Windows:


```powershell
.\mvnw.cmd test
```


## Installation and Setup

### Prerequisites

Before running VeloVerse, install:

- Java 21
- PostgreSQL
- Git

### 1. Clone the Repository

```bash
git clone YOUR_REPOSITORY_URL
cd veloverse
```

### 2. Configure the Database

Create a PostgreSQL database for VeloVerse.

Configure the database connection using the project's Spring Boot application settings.

Do not commit database passwords or other credentials to GitHub.

### 3. Run the Application

On Windows, open a terminal in the project folder and run:

```powershell
.\mvnw.cmd spring-boot:run
```

### 4. Open the Website

Once the application starts successfully, open:

http://localhost:8080

## Documentation

Additional project documentation is available in the `docs` folder.

- [System Architecture](docs/BikePedia_System_Architecture.png)
- [Database Design](docs/database-design.md)
- [Image Credits](IMAGE-CREDITS.md)

## Project Status

VeloVerse's core educational and bicycle comparison features have been implemented. Further refinements and deployment preparation are ongoing.
