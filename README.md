# Student Dashboard Backend (Spring Boot)

This module implements the Student Dashboard backend using Spring Boot layered architecture concepts.

## Concepts Used
- Controller layer: REST endpoints in `StudentDashboardController`
- Service layer: orchestration/business logic in `StudentDashboardService`
- Repository layer: Spring Data JPA repositories (`StudentRepository`, `AnnouncementRepository`)
- Persistence: JPA entities with H2 (default) and MySQL profile support
- Seed data: startup bootstrap via `DataSeeder`
- DTOs: request/response contracts in `dto/`
- Global exception handling via `@RestControllerAdvice`
- Validation with Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Min`, `@Max`)

## Endpoints
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/health`
- `GET /api/student/dashboard?email=demo.student@library.local`
- `PATCH /api/student/learning/{itemId}/progress`
- `POST /api/student/announcements`

## Run
From this folder:

```bash
mvn spring-boot:run
```

From workspace root on Windows (explicit Maven path):

```powershell
& "$env:USERPROFILE\tools\apache-maven-3.9.9\bin\mvn.cmd" -f "C:\Users\gvshe\OneDrive\Desktop\edu-library\backend-spring\pom.xml" spring-boot:run
```

Default port: `8080`

If `8080` is already in use:

```powershell
# Find process using port 8080
$pid = (Get-NetTCPConnection -LocalPort 8080 -State Listen | Select-Object -First 1).OwningProcess
Get-CimInstance Win32_Process -Filter "ProcessId = $pid" | Select-Object ProcessId, Name, CommandLine

# Stop the blocking process
Stop-Process -Id $pid -Force
```

## Demo Accounts
- Student: `demo.student@library.local` / `Student@123`
- Admin: `demo.admin@library.local` / `Admin@123`

## Database Modes
- Default (H2 in-memory): uses `application.properties`
- MySQL: configure `application-mysql.properties` and run:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Forgot Password Email Setup
By default, forgot-password falls back to returning a demo token in API response when SMTP is not configured.

Set these environment variables for real email delivery:

- `MAIL_HOST` (example: `smtp.gmail.com`)
- `MAIL_PORT` (example: `587`)
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_SMTP_AUTH` (default: `true`)
- `MAIL_SMTP_STARTTLS` (default: `true`)
- `APP_MAIL_FROM` (example: `noreply@yourdomain.com`)
- `APP_RESET_BASE_URL` (frontend base URL, example: `http://localhost:5173`)

## Local Tooling Setup Notes
- Java is installed and available (`java -version` works).
- Maven is installed to `C:\Users\gvshe\tools\apache-maven-3.9.9` and added to user environment variables.
- Open a new terminal to use `mvn` directly by name.

The React frontend service `src/services/studentDashboardApi.js` now defaults to `http://localhost:8080/api`.
