PostgreSQL Migration Plan (reversible)

Goal: Change the application to connect to PostgreSQL (no data migration executed).

What changed in this branch:

- `pom.xml`: added `org.postgresql:postgresql` dependency.
- `src/main/resources/application.properties`: switched JDBC URL prefix to `jdbc:postgresql://`, set `driver-class-name` and Hibernate dialect to PostgreSQL.
- `src/main/resources/xxxxapplication.yml`: updated driver and URL to PostgreSQL defaults.

How to run locally against PostgreSQL:

1. Start Postgres locally (Docker example):

```bash
docker run --name bmp-postgres -e POSTGRES_PASSWORD=pass -e POSTGRES_USER=root -e POSTGRES_DB=jwt -p 5432:5432 -d postgres:15
```

2. Set environment variables (example):

Windows PowerShell:

```powershell
$env:DB_HOST = 'localhost'
$env:DB_PORT = '5432'
$env:DB_NAME = 'jwt'
$env:DB_USER = 'root'
$env:DB_PASSWORD = 'pass'
$env:SERVER_PORT = '8080'
```

3. Build and run:

```bash
mvn clean package
mvn spring-boot:run
```

Reverting to MySQL (quick):

- Restore `spring.datasource.url` to `jdbc:mysql://...` and `spring.jpa.properties.hibernate.dialect` to `org.hibernate.dialect.MySQL8Dialect` in `application.properties` (or switch env vars back to a MySQL host/port).
- Optionally remove the `postgresql` dependency from `pom.xml` and re-add MySQL connection details.

Notes and recommendations:

- This change only updates connection settings and adds the JDBC driver. No schema/data conversion included.
- If you later need to migrate data, consider using `pgloader` or `mysqldump` + `pg_restore` workflows, or a migration tool like Flyway for controlled schema evolution.
