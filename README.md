# Radarr Catalog

![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-Ready-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-22-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-6-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.4-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-Alpine-009639?style=for-the-badge&logo=nginx&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)

A self-hosted, full-stack web application designed to browse and download movies directly from a local Radarr library. Structured as a monorepo, it combines a lightweight **Spring Boot** REST API with a modern, dark-mode **Angular** web client, fully containerized for `linux/amd64` home server deployments.

---

## 🏗️ Architecture & Tech Stack

### Backend (`Radarr-Catalog-Backend`)
- **Runtime & Framework:** Java 21 & Spring Boot (Spring Web)
- **HTTP Client:** Spring `RestClient` (Radarr API v3 integration)
- **Data Modeling:** Java `record` DTOs and Jackson deserialization

### Frontend (`Radarr-Catalog-Frontend`)
- **Framework:** Angular (Standalone Components & Signals)
- **Styling:** Tailwind CSS 3.4 (`@layer components` semantic classes)
- **Web Server:** Nginx (`nginx:stable-alpine`) with GZIP and immutable asset caching

### ⚡ Engineering Highlights
- **Reactive State & Instant Search:** Built with **Angular Signals** (`allMovies`, `searchQuery`, `isLoading`, `hasError`) and a `computed()` filter for zero-latency client-side searching without RxJS boilerplate.
- **Secure UTF-8 File Streaming:** Uses Spring's `UrlResource` and `ContentDisposition` builder with `StandardCharsets.UTF_8` to stream large media files while preserving accents, spaces, and special characters in filenames.
- **Path Traversal Protection:** The backend normalizes requested file paths and strictly validates that every download stays within the mounted `/movies` directory.
- **Memory-Safe Browser Downloads:** The frontend triggers native browser downloads via dynamic anchor elements, allowing multi-gigabyte `.mkv` and `.mp4` files to download without exhausting browser RAM.
- **Dynamic CORS & Zero Hardcoded Secrets:** Allowed origins and Radarr credentials are externalized via `.env` files and injected into Spring's `WebMvcConfigurer`.

---

## 📂 Repository Structure

```text
Radarr-Catalog/
├── Radarr-Catalog-Backend/
│   ├── src/main/
│   │   ├── java/com/app/radarr_catalog/
│   │   │   ├── config/           # Dynamic CORS configuration (WebMvcConfigurer)
│   │   │   ├── constants/        # Centralized headers, URIs, and paths
│   │   │   ├── controllers/      # REST endpoints (/api/movies, /download)
│   │   │   ├── dtos/             # Outgoing API response records (MovieDto)
│   │   │   ├── models/           # Radarr v3 API deserialization records
│   │   │   └── services/         # Radarr client and secure file resource loader
│   │   └── resources/
│   │       └── application.properties
│   ├── .env.example              # Environment variable template
│   ├── Dockerfile                # Multi-stage Maven + JRE build (linux/amd64)
│   └── pom.xml
├── Radarr-Catalog-Frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── constants/        # API routes and UI messages
│   │   │   ├── models/           # TypeScript DTO interfaces
│   │   │   ├── services/         # HTTP client and download trigger service
│   │   │   ├── app.ts            # Main Signal-based catalog component
│   │   │   └── app.html          # Modern Angular control flow template
│   │   ├── environments/         # Dev & Prod environment files
│   │   └── styles.css            # Global styles and Tailwind @layer components
│   ├── default.conf              # Nginx server block (SPA fallback & security headers)
│   ├── nginx.conf                # Nginx worker and GZIP configuration
│   ├── Dockerfile                # Multi-stage Node 22 + Nginx build (linux/amd64)
│   └── tailwind.config.js
├── .gitignore                    # Global monorepo ignore rules
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** & Maven (or use the included `./mvnw` wrapper)
- **Node.js (v22+)** & **Angular CLI** (`npm install -g @angular/cli`)
- A running **Radarr** instance with an active API Key

### 1. Backend Environment Setup
Navigate to `Radarr-Catalog-Backend/`, copy `.env.example` to `.env`, and fill in your credentials:

~~~env
# CORS
CORS_ALLOWED_ORIGINS=http://localhost:4200,http,http://YOUR_SERVER_IP:YOUR_SERVER_PORT

# Radarr
RADARR_URL=http://YOUR_RADARR_IP:7878/api/v3
RADARR_API_KEY=your_radarr_api_key_here
~~~

### 2. Frontend Environment Setup
- **Development (`src/environments/environment.ts`):** Points to `http://localhost:8080` by default.
- **Production (`src/environments/environment.prod.ts`):** Update `apiUrl` with your deployed backend URL before building:

~~~typescript
export const environment = {
    production: true,
    apiUrl: 'http://YOUR_SERVER_IP:YOUR_SERVER_PORT'
};
~~~

### 3. Local Execution

**Run the Backend (port `8080`):**
~~~bash
cd Radarr-Catalog-Backend
./mvnw spring-boot:run
~~~

**Run the Frontend (port `4200`):**
~~~bash
cd Radarr-Catalog-Frontend
npm install
ng serve
~~~

*(Tip: To test the local frontend against your remote production backend, run `ng serve -c production`.)*

## 🐳 Docker Deployment

Both services include multi-stage `Dockerfile` definitions optimized for `linux/amd64` servers (e.g., Intel/AMD mini PCs). You can build and run the entire stack locally using Docker Compose.

### 1. Configure `docker-compose.yml`

Make sure `Radarr-Catalog-Backend/.env` is configured and update the host media volume path (`/path/to/your/movies`) in `docker-compose.yml`:

~~~yaml
services:
  radarr-catalog-backend:
    build:
      context: ./Radarr-Catalog-Backend
      dockerfile: Dockerfile
    container_name: radarr-catalog-backend
    ports:
      - "8092:8080"
    env_file:
      - ./Radarr-Catalog-Backend/.env
    volumes:
      - /path/to/your/movies:/movies
    restart: always

  radarr-catalog-frontend:
    build:
      context: ./Radarr-Catalog-Frontend
      dockerfile: Dockerfile
    container_name: radarr-catalog-frontend
    ports:
      - "8083:80"
    depends_on:
      - radarr-catalog-backend
    restart: always
~~~

### 2. Build & Start the Stack

From the root directory (`Radarr-Catalog/`), build both images and start the containers in detached mode:

~~~bash
docker compose up -d --build
~~~

To stop and remove the containers:

~~~bash
docker compose down
~~~

The catalog will be available at `http://YOUR_SERVER_IP:YOUR_SERVER_PORT` and the API at `http://YOUR_SERVER_IP:YOUR_SERVER_PORT/api/movies`.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing
1. Create your Feature Branch (`git checkout -b feat/amazing-feature`)
2. Commit your Changes using Conventional Commits (`git commit -m 'feat(frontend): add release year filter'`)
3. Push to the Branch (`git push origin feat/amazing-feature`)
4. Open a Pull Request.