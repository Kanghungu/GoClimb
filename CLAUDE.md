# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AppClimb is a climbing gym management platform consisting of three sub-projects:
- `appclimb-backend` — Spring Boot 3.2 REST API (Java 17)
- `appclimb-admin` — React + Vite + Tailwind admin web dashboard
- `appclimb-android` — Kotlin + Jetpack Compose Android app (in development)

**Deployed services:**
- Backend: `https://goclimb-s8c2.onrender.com` (Render free tier — 30~60s cold start after 15min inactivity)
- Admin web: `https://go-climb.vercel.app` (Vercel)
- Database: Supabase PostgreSQL (ap-northeast-2) via Session Pooler

---

## Commands

### Backend (`appclimb-backend/`)
```bash
# Run locally
./gradlew bootRun --args='--spring.profiles.active=local'

# Build JAR
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.appclimb.SomeTest"

# Build Docker image
docker build -t appclimb-backend .
```

### Admin Web (`appclimb-admin/`)
```bash
npm install
npm run dev       # dev server
npm run build     # production build
npm run preview   # preview production build
```

### Android (`appclimb-android/`)
Build via Android Studio or:
```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

---

## Architecture

### System Flow
```
Android App (Kotlin)  ──┐
                         ├── HTTPS REST API ──► Spring Boot ──► PostgreSQL (Supabase)
React Admin Web (Vite) ──┘
```

### Role-Based Access
| Role | Access |
|------|--------|
| `ADMIN` | Approves/rejects gym join requests; full gym CRUD |
| `MANAGER` | Manages their gym's schedules, events, colors, sectors, staff |
| `USER` | App-only; climbing records and favorites |

**Manager onboarding:** POST `/api/auth/apply` creates the account + join request in one step. ADMIN approves → Gym is created + user promoted to MANAGER + GymManager record linked.

### Authentication
JWT (1-hour access token, no refresh token). Payload contains `userId`, `email`, `role`. Sent as `Authorization: Bearer {token}`.

### Backend Package Structure (`com.appclimb`)
- `config/` — `SecurityConfig`, `LocalAdminDataInitializer` (seeds an ADMIN account on local profile)
- `security/` — `JwtTokenProvider`, `JwtAuthenticationFilter`
- `controller/` — REST controllers per domain
- `service/` — business logic
- `domain/` — JPA entities
- `dto/` — request/response DTOs
- `repository/` — Spring Data JPA repositories
- `exception/` — global exception handling

### Admin Web Structure (`src/`)
- `api/axios.js` — shared Axios instance (attaches JWT from Zustand store)
- `store/authStore.js` — Zustand store for auth state
- `pages/` — route-level components: `auth/`, `admin/` (ADMIN views), `gym/`, `manager/`, `schedule/`, `sector/`, `color/`, `event/`
- `components/` — shared UI components

### Android Structure (`com.appclimb`)
- `data/` — `api/` (Retrofit), `model/`, `repository/`
- `ui/` — `auth/`, `gym/`, `calendar/`, `record/`, `theme/`
- `navigation/` — Compose Navigation graph
- `util/`
- Hilt for DI, DataStore for token persistence, Retrofit + OkHttp for networking

### Database
`ddl-auto: none` — schema is managed manually in Supabase. Key tables: `users`, `gyms`, `gym_managers`, `gym_join_requests`, `gym_staff`, `sectors`, `difficulty_colors`, `setting_schedules`, `events`, `climbing_records`, `record_entries`, `user_favorite_gyms`.

### Backend Profiles
- `local` — uses `application-local.yml`; `LocalAdminDataInitializer` seeds an ADMIN account
- `prod` — uses `application-prod.yml`; datasource/JWT configured via environment variables (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `PORT`)
