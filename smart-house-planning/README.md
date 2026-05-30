# Smart 2D House Planning and Graph Web Application

Full-stack Java web application for generating interactive 2D house floor plans from user requirements.

## Tech Stack

- **Frontend:** HTML, CSS, JavaScript, Bootstrap 5, HTML5 Canvas
- **Backend:** Java 17, Spring Boot 3.2
- **Database:** H2 (dev) / MySQL (production)
- **Build:** Maven
- **Server:** Embedded Tomcat (WAR deployable to external Tomcat)

## Features

- User registration, login, logout, forgot/reset password
- Dashboard with saved plans, PDF export, PNG download
- Requirement form (plot size, bedrooms, bathrooms, kitchen, hall, parking, staircase, balcony)
- Auto-generated 2D grid layout with walls, doors, windows, labels
- Interactive canvas: drag, resize, zoom, grid snap, real-time area calculation
- Admin module: manage users and plans, usage counts
- OOP design: `Renderable`, `Exportable`, `RoomAllocator` interfaces; `AbstractRoom` base class; room type inheritance

## Project Structure

```
src/main/java/com/houseplan/
├── controller/     # MVC + REST API
├── service/        # Business logic
├── repository/     # JPA repositories
├── model/          # Entities, DTOs, room hierarchy
├── config/         # Security, data init
└── utils/          # PlanLayoutGenerator
src/main/resources/
├── templates/      # Thymeleaf views
├── static/         # CSS, JS
└── application*.properties
```

## Quick Start

### Prerequisites

- JDK 17+
- Maven 3.8+

### Run (H2 in-memory — no MySQL setup)

```bash
cd smart-house-planning
mvn spring-boot:run
```

Open http://localhost:8080

**Default admin:** `admin` / `admin123`

### MySQL

1. Create database (or use `schema.sql`)
2. Run with profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

3. Update `application-mysql.properties` with your credentials.

### Deploy to Tomcat

```bash
mvn clean package
```

Deploy `target/smart-house-planning-1.0.0.war` to Apache Tomcat.

## Usage Flow

1. Register or login
2. **Dashboard → New Plan**
3. Enter plot dimensions and room requirements
4. Click **Generate Layout** — canvas updates
5. Drag/resize rooms on canvas
6. **Save to Database** — persists plan and rooms
7. Export **PDF** or **PNG**

## API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/generate` | POST | Generate layout JSON from requirements |
| `/api/plans/{id}/layout` | POST | Save canvas layout changes |

## Future Scope

- AI room suggestions, Vastu recommendations, cost estimation
- 3D visualization, mobile app, real-time collaboration

## License

Educational / project use.
