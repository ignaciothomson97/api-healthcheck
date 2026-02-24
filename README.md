# API Healthcheck Monitor

A Java-based application that periodically monitors the status of various APIs and persists the results into a PostgreSQL database, with automated CSV exports.

## Overview

This tool performs parallel health checks on pre-defined API endpoints. It tracks the HTTP response status, whether the API is "up" (status code 2xx), and the last check timestamp.

### Key Features

- **Parallel Health Checks**: Monitors multiple APIs concurrently for efficiency.
- **Persistent Storage**: Saves check results in a PostgreSQL database.
- **Automated Scheduler**:
  - **Status Checks**: Every 60 seconds.
  - **CSV Export & Cleanup**: Every 7 days, the database records are exported to a CSV file and the table is cleared to maintain performance.
- **CSV Backups**: Automatically generates CSV files in the `db-backup/` directory.

## Monitored APIs

The application currently monitors the following services:

- **Reddit** (`https://www.redditstatus.com/api/v2/status.json`)
- **LinkedIn** (`https://www.linkedin-apistatus.com/api/v2/status.json`)
- **Discord** (`https://discordstatus.com/api/v2/status.json`)
- **GitHub** (`https://www.githubstatus.com/api/v2/status.json`)
- **Meta** (`https://metastatus.com/`)
- **Autodesk** (`https://health.autodesk.com/`)

## Requirements

- **Java 21** or higher.
- **Maven** (for dependency management and building).
- **PostgreSQL** (to persist status data).

## Setup & Run

### 1. Database Configuration

1.  Create a PostgreSQL database.
2.  Execute the initialization script located at `app/src/main/resources/db/initial_table.sql` to create the `api_status` table.
3.  Configure your database credentials in `app/src/main/resources/database.properties`:

    ```properties
    db.url=jdbc:postgresql://localhost:5432/your_database
    db.username=your_username
    db.password=your_password
    ```

### 2. Build and Run

Navigate to the `app` directory and use Maven:

```bash
cd app
mvn clean compile
mvn exec:java -Dexec.mainClass="cl.apihealthcheck.Main"
```

## Project Structure

```text
.
├── README.md
├── db-backup/                  # Automated CSV backups
└── app
    ├── pom.xml                 # Maven configuration
    └── src
        └── main
            ├── java
            │   └── cl/apihealthcheck
            │       ├── Main.java           # Entry point
            │       ├── config/             # DB Connection logic
            │       ├── constants/          # API endpoints and constants
            │       ├── entity/             # Data models (POJOs)
            │       ├── helper/             # Scheduler, DataHandler, and Request logic
            │       ├── records/            # Java Records (RestResponse, ScheduledJob)
            │       ├── repository/         # Data persistence (JDBC)
            │       └── service/            # Healthcheck service logic
            └── resources
                ├── database.properties     # DB config
                └── db/
                    └── initial_table.sql   # SQL schema
```

## Scripts

- `mvn clean compile`: Compiles the project.
- `mvn exec:java -Dexec.mainClass="cl.apihealthcheck.Main"`: Starts the monitor.
- `TODO`: Add a packaging script (e.g., `mvn package`) to generate an executable JAR.

## Tests

- `TODO`: Implement unit tests for `StatusCheckImpl` and `RequestHandler`.

## License

- `TODO`: Specify a license (e.g., MIT, Apache 2.0).
