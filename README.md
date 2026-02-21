# API Healthcheck Monitor

A Java-based application that periodically monitors the status of various APIs (Reddit, LinkedIn, Discord, GitHub) and persists the results into a PostgreSQL database.

## Overview

This tool performs parallel health checks on pre-defined API endpoints every 60 seconds. It tracks the HTTP response status, whether the API is "up" (status code 2xx), and the last check timestamp.

## Requirements

*   **Java 21** or higher.
*   **Maven** (for dependency management and building).
*   **PostgreSQL** (to persist status data).

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
└── app
    ├── pom.xml                     # Maven configuration
    └── src
        └── main
            ├── java
            │   └── cl/apihealthcheck
            │       ├── Main.java           # Entry point
            │       ├── config/             # DB Connection logic
            │       ├── constants/          # API endpoints
            │       ├── entity/             # Data models
            │       ├── helper/             # Scheduler and Request logic
            │       ├── repository/         # Data persistence (JDBC)
            │       └── service/            # Healthcheck logic
            └── resources
                ├── database.properties     # DB config
                └── db/
                    └── initial_table.sql   # SQL schema
```

## Scripts

*   `mvn clean compile`: Compiles the project.
*   `mvn exec:java -Dexec.mainClass="cl.apihealthcheck.Main"`: Starts the monitor.
*   `TODO`: Add a packaging script (e.g., `mvn package`) to generate an executable JAR.

## Environment Variables / Configuration

The application uses `app/src/main/resources/database.properties` for configuration.
*   `db.url`: JDBC URL for PostgreSQL.
*   `db.username`: Database user.
*   `db.password`: Database password.

## Tests

*   `TODO`: No tests detected. Implement unit tests for `StatusCheckImpl` and `RequestHandler`.

## License

*   `TODO`: Specify a license (e.g., MIT, Apache 2.0).
