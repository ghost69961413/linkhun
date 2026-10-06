# LinkHub

LinkHub is a React + TypeScript + Vite frontend and Spring Boot 3 backend for a professional networking app. The repository is organized as a monorepo:

```text
frontend/                 React 19, TypeScript, Vite, pnpm
src/                      Spring Boot backend source
src/main/resources/       Local and production Spring configuration
Dockerfile                Java 21 production image
render.yaml               Render web service blueprint
```

## Requirements

- Java 21
- MySQL 8 (an existing database reachable from the backend host)
- Node.js 20+ and pnpm 10+
- Maven (or the included Maven wrapper)

## Local setup

1. Create a MySQL database named `linkhub_db` and a least-privilege application user.
2. Set the backend environment variables from `.env.backend.example`. Spring Boot does not load `.env` files automatically; export these in your shell or configure them in your IDE.
3. Generate a unique JWT signing key for each environment:

   ```sh
   openssl rand -base64 64
   ```

   Set the output as `JWT_SECRET`. The same secret must be used by both JWT implementations in this backend.
4. Start the backend:

   ```sh
   ./mvnw spring-boot:run
   ```

5. Configure and start the frontend:

   ```sh
   cd frontend
   cp .env.example .env.local
   pnpm install
   pnpm dev
   ```

   `VITE_API_URL` is the full API root, including `/api`, such as `http://localhost:8080/api`.

## Database

Production uses the existing MySQL schema and sets Hibernate to `validate`. Create/update the schema through the project's schema process before deployment. `DB_URL` must be a JDBC URL reachable from Render, for example `jdbc:mysql://db.example.net:3306/linkhub_db?useSSL=true&requireSSL=true`. A database running only on a developer's `localhost` cannot be reached by Vercel or Render. Configure network allowlisting and TLS at the database provider.

Required backend variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`, and `LINKHUB_PUBLIC_BASE_URL`. `PORT` is supplied by Render. See `.env.backend.example` and `src/main/resources/application-prod.properties`.

## Deploy frontend to Vercel

1. Push this repository to GitHub.
2. Import it in Vercel and set **Root Directory** to `frontend`.
3. Framework preset: Vite. Build command: `pnpm build`. Output directory: `dist`. Install command: `pnpm install --frozen-lockfile`.
4. Add the Vercel environment variable `VITE_API_URL=https://YOUR_RENDER_SERVICE.onrender.com/api` for Production (and Preview if wanted), then redeploy.
5. Add the exact Vercel origin, such as `https://linkhub.vercel.app`, to backend `CORS_ALLOWED_ORIGINS`.

`frontend/vercel.json` rewrites app routes to the SPA entry point, so refreshes on `/profile/:id`, `/projects/:id`, and other client routes return the application.

## Deploy backend to Render

1. In Render, create a Blueprint from the GitHub repository and select the repository root where `render.yaml` and `Dockerfile` live. Or create a Docker Web Service with the same root.
2. Add the required secrets/values: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, a fresh `JWT_SECRET` from `openssl rand -base64 64`, `CORS_ALLOWED_ORIGINS`, and `LINKHUB_PUBLIC_BASE_URL` (the Render service URL). Set `SPRING_PROFILES_ACTIVE=prod`.
3. Deploy and confirm `https://YOUR_RENDER_SERVICE.onrender.com/actuator/health` reports `UP`.
4. Set Vercel `VITE_API_URL` to the deployed API URL plus `/api` and redeploy the frontend.

Render describes its free instances as suitable for testing/hobby projects, not production applications. Free services can spin down when idle and do not support persistent disks. This backend currently writes uploaded media to local disk, so uploaded profile/post/project media will not be durable across instance replacement/redeploy without a durable object-storage integration. Treat the free deployment as a student/demo environment; use a paid persistent backend or integrate object storage before serving production users. Database records are separate and use MySQL.

## Environment variable reference

| Variable | Used by | Purpose |
| --- | --- | --- |
| `VITE_API_URL` | Vercel/frontend | Public backend API root including `/api` |
| `DB_URL` | Backend | MySQL JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | Backend | MySQL application credentials |
| `JWT_SECRET` | Backend | Base64-encoded strong JWT signing secret; keep stable per environment |
| `PORT` | Backend host | HTTP listen port (defaults to 8080 locally) |
| `CORS_ALLOWED_ORIGINS` | Backend | Comma-separated exact frontend origins; no wildcard with credentials |
| `LINKHUB_PUBLIC_BASE_URL` | Backend | Public backend origin used for uploaded-media links |
| `LINKHUB_UPLOAD_DIR` | Backend | Local upload directory (ephemeral on Render free) |
| `JWT_EXPIRATION` | Backend | Token lifetime in milliseconds; defaults to 86400000 |

## Build verification

```sh
cd frontend && pnpm build
cd .. && ./mvnw clean package -DskipTests
```

Deployment credentials, a publicly reachable MySQL endpoint, and deployed URLs must be configured before real register/login/profile/project/post/connection/message flows can be tested on production.
