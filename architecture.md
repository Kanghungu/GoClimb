# AppClimb 시스템 설계 문서

> 클라이머용 앱 + 지점 관리자 웹 플랫폼

---

## 1. 기술 스택

| 영역 | 기술 | 배포 |
|------|------|------|
| 백엔드 API | Spring Boot 3.x (Java 17) | Render (무료) |
| 데이터베이스 | PostgreSQL | Supabase (무료) |
| 안드로이드 앱 | Kotlin + Jetpack Compose | Google Play (추후) |
| 관리자 웹 | React + Vite | Vercel (무료) |
| 코드 관리 | GitHub | - |
| 개발 중 터널 | ngrok | 무료 |

---

## 2. 시스템 구조

```
┌─────────────────┐     ┌─────────────────┐
│  Android App    │     │  React 관리자웹  │
│  (Kotlin)       │     │  (Vercel)        │
└────────┬────────┘     └────────┬────────┘
         │  HTTPS REST API       │
         └──────────┬────────────┘
                    ▼
         ┌─────────────────────┐
         │  Spring Boot API    │
         │  (Render)           │
         └──────────┬──────────┘
                    │ JDBC
                    ▼
         ┌─────────────────────┐
         │  PostgreSQL         │
         │  (Supabase)         │
         └─────────────────────┘
```

---

## 3. ERD (Entity Relationship Diagram)

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│   users      │       │  user_favorite   │       │    gyms      │
│──────────────│       │  _gyms           │       │──────────────│
│ id (PK)      │◄──────│──────────────────│──────►│ id (PK)      │
│ email        │       │ id (PK)          │       │ name         │
│ password     │       │ user_id (FK)     │       │ address      │
│ nickname     │       │ gym_id (FK)      │       │ description  │
│ role         │       │ created_at       │       │ created_at   │
│ created_at   │       └──────────────────┘       └──────┬───────┘
└──────┬───────┘                                         │
       │                                                 │
       │  ┌───────────────────┐              ┌───────────┴──────────┐
       │  │  climbing_records │              │      sectors         │
       │  │───────────────────│              │──────────────────────│
       └─►│ id (PK)           │              │ id (PK)              │
          │ user_id (FK)      │              │ gym_id (FK)          │
          │ gym_id (FK)       │              │ name (예: A존, B존)   │
          │ record_date       │              │ description          │
          │ created_at        │              └───────────┬──────────┘
          └──────┬────────────┘                          │
                 │                           ┌───────────┴──────────┐
                 │                           │  setting_schedules   │
  ┌──────────────┴──────┐                   │──────────────────────│
  │  record_entries     │                   │ id (PK)              │
  │─────────────────────│                   │ gym_id (FK)          │
  │ id (PK)             │                   │ sector_id (FK)       │
  │ record_id (FK)      │                   │ setting_date         │
  │ color_id (FK)       │                   │ description          │
  │ planned_count       │                   │ created_at           │
  │ completed_count     │                   └──────────────────────┘
  └─────────────────────┘
  
  ┌──────────────────────┐       ┌──────────────────────┐
  │  difficulty_colors   │       │       events         │
  │──────────────────────│       │──────────────────────│
  │ id (PK)              │       │ id (PK)              │
  │ gym_id (FK)          │       │ gym_id (FK)          │
  │ color_name (예:노랑) │       │ title                │
  │ color_hex (#FFFF00)  │       │ description          │
  │ level_order (순서)   │       │ start_date           │
  └──────────────────────┘       │ end_date             │
                                  │ created_at           │
  ┌──────────────────────┐       └──────────────────────┘
  │   gym_managers       │
  │──────────────────────│
  │ id (PK)              │
  │ user_id (FK)         │
  │ gym_id (FK)          │
  │ created_at           │
  └──────────────────────┘
```

---

## 4. 테이블 상세 설명

### users
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| email | VARCHAR(100) UNIQUE | 로그인 이메일 |
| password | VARCHAR(255) | bcrypt 암호화 |
| nickname | VARCHAR(50) | 닉네임 |
| role | VARCHAR(20) | USER / MANAGER / ADMIN |
| created_at | TIMESTAMP | 가입일 |

### gyms (클라이밍 지점)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| name | VARCHAR(100) | 지점명 |
| address | VARCHAR(255) | 주소 |
| description | TEXT | 지점 소개 |
| created_at | TIMESTAMP | 등록일 |

### sectors (섹터)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| gym_id | BIGINT FK | 소속 지점 |
| name | VARCHAR(50) | 섹터명 (A존, B존 등) |
| description | TEXT | 섹터 설명 |

### difficulty_colors (난이도 색깔 - 지점별 커스텀)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| gym_id | BIGINT FK | 소속 지점 |
| color_name | VARCHAR(30) | 색깔명 (노랑, 파랑 등) |
| color_hex | VARCHAR(7) | HEX코드 (#FFFF00) |
| level_order | INT | 난이도 순서 (1=쉬움) |

### setting_schedules (세팅 일정)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| gym_id | BIGINT FK | 소속 지점 |
| sector_id | BIGINT FK | 대상 섹터 |
| setting_date | DATE | 세팅 변경 날짜 |
| description | TEXT | 메모 |
| created_at | TIMESTAMP | 등록일 |

### climbing_records (운동 기록 - 날짜별 세션)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| user_id | BIGINT FK | 사용자 |
| gym_id | BIGINT FK | 방문 지점 |
| record_date | DATE | 운동 날짜 |
| created_at | TIMESTAMP | 등록일 |

### record_entries (난이도별 기록)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| record_id | BIGINT FK | 소속 운동기록 |
| color_id | BIGINT FK | 난이도 색깔 |
| planned_count | INT | 목표 개수 |
| completed_count | INT | 완료 개수 |

### events (지점 이벤트)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| gym_id | BIGINT FK | 소속 지점 |
| title | VARCHAR(100) | 이벤트 제목 |
| description | TEXT | 이벤트 내용 |
| start_date | DATE | 시작일 |
| end_date | DATE | 종료일 |
| created_at | TIMESTAMP | 등록일 |

---

## 5. API 명세

### 인증 (Auth)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| POST | /api/auth/register | 회원가입 | 누구나 |
| POST | /api/auth/login | 로그인 (JWT 반환) | 누구나 |
| POST | /api/auth/refresh | 토큰 갱신 | 누구나 |

### 지점 (Gym)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/gyms | 전체 지점 목록 | 누구나 |
| GET | /api/gyms/{id} | 지점 상세 | 누구나 |
| POST | /api/gyms | 지점 등록 | ADMIN |
| PUT | /api/gyms/{id} | 지점 수정 | MANAGER |

### 즐겨찾기 (Favorite)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/favorites | 내 즐겨찾기 목록 | USER |
| POST | /api/favorites/{gymId} | 즐겨찾기 추가 | USER |
| DELETE | /api/favorites/{gymId} | 즐겨찾기 삭제 | USER |

### 세팅 일정 (Setting Schedule)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/gyms/{gymId}/schedules | 지점 세팅 일정 목록 | 누구나 |
| GET | /api/gyms/{gymId}/schedules?month=2025-06 | 월별 캘린더 조회 | 누구나 |
| POST | /api/gyms/{gymId}/schedules | 세팅 일정 등록 | MANAGER |
| PUT | /api/gyms/{gymId}/schedules/{id} | 세팅 일정 수정 | MANAGER |
| DELETE | /api/gyms/{gymId}/schedules/{id} | 세팅 일정 삭제 | MANAGER |

### 운동 기록 (Climbing Record)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/records | 내 운동기록 목록 | USER |
| GET | /api/records?month=2025-06 | 월별 기록 조회 | USER |
| POST | /api/records | 운동기록 등록 | USER |
| PUT | /api/records/{id} | 운동기록 수정 | USER |
| DELETE | /api/records/{id} | 운동기록 삭제 | USER |

### 이벤트 (Event)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/gyms/{gymId}/events | 지점 이벤트 목록 | 누구나 |
| POST | /api/gyms/{gymId}/events | 이벤트 등록 | MANAGER |
| PUT | /api/gyms/{gymId}/events/{id} | 이벤트 수정 | MANAGER |
| DELETE | /api/gyms/{gymId}/events/{id} | 이벤트 삭제 | MANAGER |

### 난이도 색깔 (Difficulty Color)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/gyms/{gymId}/colors | 지점 난이도 목록 | 누구나 |
| POST | /api/gyms/{gymId}/colors | 난이도 등록 | MANAGER |
| PUT | /api/gyms/{gymId}/colors/{id} | 난이도 수정 | MANAGER |
| DELETE | /api/gyms/{gymId}/colors/{id} | 난이도 삭제 | MANAGER |

---

## 6. 개발 순서 (권장)

```
Phase 1 - 백엔드 기반
  ├── Spring Boot 프로젝트 세팅
  ├── DB 연결 (Supabase PostgreSQL)
  ├── User 엔티티 + 회원가입/로그인 (JWT)
  └── Gym, Sector 엔티티 + 기본 CRUD

Phase 2 - 핵심 기능
  ├── 세팅 일정 등록/조회 API
  ├── 운동 기록 등록/조회 API
  └── 즐겨찾기 API

Phase 3 - 안드로이드 앱
  ├── 로그인/회원가입 화면
  ├── 즐겨찾기 지점 목록
  ├── 캘린더 (세팅 일정 표시)
  └── 운동 기록 입력/조회

Phase 4 - 관리자 웹 (React)
  ├── 로그인
  ├── 세팅 일정 관리
  ├── 이벤트 관리
  └── 섹터/난이도 관리

Phase 5 - 배포
  ├── Render (Spring Boot)
  ├── Supabase (PostgreSQL)
  └── Vercel (React)
```

---

## 7. JWT 토큰 전략

- **Access Token**: 유효기간 1시간, Authorization 헤더로 전달
- **Refresh Token**: 유효기간 30일, DB 저장
- 앱/웹 모두 동일한 토큰 방식 사용

---

*문서 최초 작성: 2026-04-15*
