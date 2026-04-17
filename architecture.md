# AppClimb 시스템 설계 문서

> 클라이머용 앱 + 지점 관리자 웹 플랫폼

---

## 1. 기술 스택

| 영역 | 기술 | 배포 URL |
|------|------|----------|
| 백엔드 API | Spring Boot 3.2 (Java 17) | https://goclimb-s8c2.onrender.com |
| 데이터베이스 | PostgreSQL 17 | Supabase (ap-northeast-2) |
| 안드로이드 앱 | Kotlin + Jetpack Compose | Google Play (추후) |
| 관리자 웹 | React + Vite + Tailwind CSS | https://go-climb.vercel.app |
| 코드 관리 | GitHub | - |

> **Render 무료 플랜**: 비활성 15분 후 spin-down. 첫 요청 시 약 30~60초 소요.
> **DB 연결**: Supabase Session Pooler (IPv4) 사용 — `aws-1-ap-northeast-2.pooler.supabase.com:5432`

---

## 2. 시스템 구조

```
┌─────────────────┐     ┌──────────────────────────┐
│  Android App    │     │  React 관리자 웹 (Vercel) │
│  (Kotlin)       │     │  go-climb.vercel.app      │
└────────┬────────┘     └────────────┬─────────────┘
         │  HTTPS REST API           │
         └──────────────┬────────────┘
                        ▼
           ┌─────────────────────────┐
           │  Spring Boot API        │
           │  goclimb-s8c2.onrender  │
           └──────────┬──────────────┘
                      │ JDBC (Session Pooler)
                      ▼
           ┌─────────────────────────┐
           │  PostgreSQL (Supabase)  │
           └─────────────────────────┘
```

---

## 3. 역할 (Role) 구조

| 역할 | 설명 | 관리자 웹 메뉴 |
|------|------|--------------|
| ADMIN | 웹 관리자 | 지점 요청 승인 |
| MANAGER | 지점 관리자 | 세팅일정 / 이벤트 / 난이도색깔 / 섹터관리 / 직원관리 |
| USER | 일반 사용자 | 관리자 웹 접근 불가 (앱 전용) |

> **직원(gym_staff)**: SETTER / TEACHER / FRONT / MANAGER_STAFF — DB 기록·분류용이며 별도 로그인 없음

### 지점 관리자 온보딩 흐름

```
신청자 → /apply 페이지 (이름/이메일/비번 + 지점정보 입력)
       → POST /api/auth/apply (계정 생성 + 지점 신청 동시 처리)
       → ADMIN이 관리자 웹에서 승인
       → 자동으로: Gym 생성 + 신청자 MANAGER 역할 부여 + GymManager 연결
       → 신청자가 관리자 웹 로그인 가능
```

---

## 4. ERD

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│   users      │       │ user_favorite    │       │    gyms      │
│──────────────│       │ _gyms            │       │──────────────│
│ id (PK)      │◄──────│──────────────────│──────►│ id (PK)      │
│ email        │       │ id (PK)          │       │ name         │
│ password     │       │ user_id (FK)     │       │ address      │
│ nickname     │       │ gym_id (FK)      │       │ description  │
│ role         │       │ created_at       │       │ created_at   │
│ created_at   │       └──────────────────┘       └──────┬───────┘
└──────┬───────┘                                         │
       │                                        ┌────────┴──────────────────────┐
       │  ┌────────────────────┐                │                               │
       │  │  gym_join_requests │      ┌─────────┴──────┐          ┌────────────┴─────┐
       └─►│────────────────────│      │    sectors      │          │   gym_managers   │
          │ id (PK)            │      │────────────────│          │──────────────────│
          │ requester_id (FK)  │      │ id (PK)        │          │ id (PK)          │
          │ gym_name           │      │ gym_id (FK)    │          │ user_id (FK)     │
          │ gym_address        │      │ name           │          │ gym_id (FK)      │
          │ gym_description    │      │ description    │          │ created_at       │
          │ status             │      └────────┬───────┘          └──────────────────┘
          │ created_at         │               │
          │ reviewed_at        │      ┌────────┴───────────┐
          └────────────────────┘      │  setting_schedules │
                                      │────────────────────│
  ┌─────────────────┐                 │ id (PK)            │
  │   gym_staff     │                 │ gym_id (FK)        │
  │─────────────────│                 │ sector_id (FK)     │
  │ id (PK)         │                 │ setting_date       │
  │ gym_id (FK)─────┼────────────────►│ description        │
  │ name            │                 │ created_at         │
  │ staff_role      │                 └────────────────────┘
  │ note            │
  │ created_at      │      ┌──────────────────┐       ┌──────────────────┐
  └─────────────────┘      │ difficulty_colors│       │     events       │
                            │──────────────────│       │──────────────────│
                            │ id (PK)          │       │ id (PK)          │
                            │ gym_id (FK)      │       │ gym_id (FK)      │
                            │ color_name       │       │ title            │
                            │ color_hex        │       │ description      │
                            │ level_order      │       │ start_date       │
                            └──────────────────┘       │ end_date         │
                                                        │ created_at       │
  ┌──────────────────────┐                             └──────────────────┘
  │  climbing_records    │
  │──────────────────────│
  │ id (PK)              │
  │ user_id (FK)         │       ┌──────────────────┐
  │ gym_id (FK)          │──────►│  record_entries  │
  │ record_date          │       │──────────────────│
  │ created_at           │       │ id (PK)          │
  └──────────────────────┘       │ record_id (FK)   │
                                  │ color_id (FK)    │
                                  │ planned_count    │
                                  │ completed_count  │
                                  └──────────────────┘
```

---

## 5. 테이블 상세

### users
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| email | VARCHAR(100) UNIQUE | 로그인 이메일 |
| password | VARCHAR(255) | bcrypt 암호화 |
| nickname | VARCHAR(50) | 닉네임 |
| role | VARCHAR(20) | `USER` / `MANAGER` / `ADMIN` |
| created_at | TIMESTAMP | 가입일 |

### gym_join_requests (지점 가입 신청)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| requester_id | BIGINT FK | 신청한 사용자 |
| gym_name | VARCHAR(100) | 신청 지점명 |
| gym_address | VARCHAR(255) | 주소 |
| gym_description | TEXT | 지점 소개 |
| status | VARCHAR(20) | `PENDING` / `APPROVED` / `REJECTED` |
| created_at | TIMESTAMP | 신청일 |
| reviewed_at | TIMESTAMP | 처리일 |

### gym_staff (직원 역할 — DB 기록용, 별도 로그인 없음)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| gym_id | BIGINT FK | 소속 지점 |
| name | VARCHAR(50) | 직원 이름 |
| staff_role | VARCHAR(20) | `SETTER` / `TEACHER` / `FRONT` / `MANAGER_STAFF` |
| note | VARCHAR(255) | 메모 (예: 월수금 근무) |
| created_at | TIMESTAMP | 등록일 |

### gyms
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| name | VARCHAR(100) | 지점명 |
| address | VARCHAR(255) | 주소 |
| description | TEXT | 지점 소개 |
| created_at | TIMESTAMP | 등록일 |

### gym_managers
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGSERIAL PK | 기본키 |
| user_id | BIGINT FK | 관리자 사용자 |
| gym_id | BIGINT FK | 담당 지점 |
| created_at | TIMESTAMP | 배정일 |

### sectors, difficulty_colors, setting_schedules, events
→ 모두 `gym_id` FK로 지점에 귀속. MANAGER만 등록/수정/삭제 가능.

### climbing_records / record_entries
→ 앱 사용자(USER)의 운동 기록. 날짜별 세션 + 난이도별 세부 기록 (시도 수 / 완등 수).

---

## 6. API 명세

### 인증 (Auth) — `/api/auth/**` 전체 공개
| Method | URL | 설명 |
|--------|-----|------|
| POST | /api/auth/register | 일반 회원가입 (USER 역할) |
| POST | /api/auth/login | 로그인 (JWT 반환) |
| POST | /api/auth/apply | **지점 가입 신청** — 계정 생성 + 지점 신청 동시 처리 |

### 지점 가입 신청 (Gym Join Request)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| POST | /api/gym-join-requests | 지점 신청 제출 | 인증된 사용자 |
| GET | /api/admin/gym-join-requests | 전체 신청 목록 | ADMIN |
| GET | /api/admin/gym-join-requests/pending | 대기 중 목록 | ADMIN |
| POST | /api/admin/gym-join-requests/{id}/approve | 신청 승인 | ADMIN |
| POST | /api/admin/gym-join-requests/{id}/reject | 신청 거절 | ADMIN |

> 승인 시 자동 처리: Gym 생성 + 신청자 role → MANAGER + GymManager 연결

### 지점 (Gym)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/gyms | 전체 지점 목록 | 누구나 |
| GET | /api/gyms/{id} | 지점 상세 | 누구나 |
| POST | /api/gyms | 지점 직접 등록 | ADMIN |
| PUT | /api/gyms/{id} | 지점 수정 | MANAGER / ADMIN |
| DELETE | /api/gyms/{id} | 지점 삭제 | ADMIN |

### 직원 관리 (Gym Staff)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/gyms/{gymId}/staff | 직원 목록 | MANAGER / ADMIN |
| POST | /api/gyms/{gymId}/staff | 직원 등록 | MANAGER |
| PUT | /api/gyms/{gymId}/staff/{id} | 직원 정보 수정 | MANAGER |
| DELETE | /api/gyms/{gymId}/staff/{id} | 직원 삭제 | MANAGER |

### 세팅 일정 (Setting Schedule)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/gyms/{gymId}/schedules | 세팅 일정 목록 | 누구나 |
| GET | /api/gyms/{gymId}/schedules?month=2025-06 | 월별 조회 | 누구나 |
| POST | /api/gyms/{gymId}/schedules | 등록 | MANAGER |
| PUT | /api/gyms/{gymId}/schedules/{id} | 수정 | MANAGER |
| DELETE | /api/gyms/{gymId}/schedules/{id} | 삭제 | MANAGER |

### 이벤트 / 난이도 색깔 / 섹터
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET/POST/PUT/DELETE | /api/gyms/{gymId}/events | 이벤트 | 조회:누구나 / 나머지:MANAGER |
| GET/POST/PUT/DELETE | /api/gyms/{gymId}/colors | 난이도 색깔 | 조회:누구나 / 나머지:MANAGER |
| GET/POST/PUT/DELETE | /api/gyms/{gymId}/sectors | 섹터 | 조회:누구나 / 나머지:MANAGER |

### 운동 기록 / 즐겨찾기 (앱 전용)
| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | /api/records?month=yyyy-MM | 월별 운동 기록 조회 | USER |
| POST | /api/records | 운동 기록 등록 | USER |
| DELETE | /api/records/{id} | 운동 기록 삭제 | USER |
| GET | /api/favorites | 즐겨찾기 목록 | USER |
| POST | /api/favorites/{gymId} | 즐겨찾기 추가 | USER |
| DELETE | /api/favorites/{gymId} | 즐겨찾기 삭제 | USER |
| GET | /api/me/gym | 내 담당 지점 조회 | MANAGER |

---

## 7. JWT 토큰

- **Access Token**: 유효기간 1시간, `Authorization: Bearer {token}` 헤더로 전달
- Payload: `userId`, `email`, `role`
- 별도 Refresh Token 없음 (현재 구현 기준)

---

## 8. Android 앱 구조

### 기술 스택
| 항목 | 버전 |
|------|------|
| Kotlin | 1.9.22 |
| Android Gradle Plugin | 8.2.2 |
| Gradle Wrapper | 8.5 (JDK 21 호환) |
| Compose BOM | 2024.02.00 |
| Hilt | 2.51.1 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| Coil | 2.6.0 |
| DataStore | 1.1.1 |
| Navigation Compose | 2.7.7 |

### 화면 구성
```
로그인 / 회원가입
└── 메인 (바텀탭 4개)
    ├── 지점 탭
    │   ├── GymListScreen    — 전체 지점 목록 + 즐겨찾기 토글
    │   └── GymDetailScreen  — 지점 상세 (정보 / 난이도색깔 / 이번달 세팅일정)
    ├── 캘린더 탭
    │   └── CalendarScreen   — 즐겨찾기 지점 선택 + 월별 세팅일정 캘린더
    ├── 기록 탭
    │   └── RecordScreen     — 월별 운동기록 조회 / 추가 (즐겨찾기 지점 기반)
    └── 프로필 탭
        └── ProfileScreen    — 이번달 통계 (방문/완등/완등률) + 기록 피드 + 로그아웃
```

### 패키지 구조
```
com.appclimb
├── data/
│   ├── api/          ApiService, NetworkModule (Hilt)
│   ├── model/        Models.kt (GymSummary, GymResponse, ClimbingRecord 등)
│   └── repository/   AuthRepository, GymRepository, RecordRepository
├── ui/
│   ├── auth/         LoginScreen, RegisterScreen, LoginViewModel
│   ├── gym/          GymListScreen, GymDetailScreen, GymViewModel, GymDetailViewModel
│   ├── calendar/     CalendarScreen, CalendarViewModel
│   ├── record/       RecordScreen, RecordViewModel
│   └── profile/      ProfileScreen, ProfileViewModel
├── navigation/       AppNavigation (루트), MainScreen (바텀탭)
└── util/             TokenManager (DataStore)
```

### 주요 설계 결정
- **즐겨찾기 기반 연동**: 캘린더/기록 화면은 즐겨찾기 지점 목록만 표시. 즐겨찾기가 없으면 안내 메시지
- **토큰 저장**: DataStore Preferences (JWT + userId + nickname + role)
- **JWT 주입**: OkHttp Interceptor에서 DataStore 토큰 자동 첨부
- **BASE_URL**: `https://goclimb-s8c2.onrender.com/api/` (BuildConfig)

---

## 9. 개발 현황

```
✅ Phase 1 - 백엔드 기반
  ├── Spring Boot 프로젝트 + Docker
  ├── DB 연결 (Supabase PostgreSQL, Session Pooler)
  ├── User 엔티티 + 회원가입/로그인 (JWT)
  └── Gym, Sector 엔티티 + 기본 CRUD

✅ Phase 2 - 핵심 기능 API
  ├── 세팅 일정 / 이벤트 / 난이도 색깔 / 섹터 CRUD
  ├── 운동 기록 / 즐겨찾기 API
  └── GymManager 배정

✅ Phase 3 - 관리자 웹 (React)
  ├── 로그인 / 역할별 분기
  ├── 웹관리자: 지점 가입 신청 승인/거절
  ├── 지점관리자: 세팅일정, 이벤트, 난이도, 섹터, 직원관리
  └── 신규 지점 신청 페이지 (/apply, 공개)

✅ Phase 4 - 배포
  ├── Render (Spring Boot Docker)
  ├── Supabase (PostgreSQL)
  └── Vercel (React)

✅ Phase 5 - 안드로이드 앱 (구현 완료, 테스트 진행 중)
  ├── 로그인 / 회원가입
  ├── 지점 목록 + 검색(이름/주소) + 즐겨찾기 필터 토글
  ├── 지점 상세 (난이도 색깔 + 세팅일정)
  ├── 캘린더 (즐겨찾기 지점 기반 세팅일정 표시)
  ├── 이벤트 (진행중/예정/종료 분류)
  ├── 운동 기록 입력/수정/삭제 (날짜 피커, 즐겨찾기 지점 기반, 난이도별 기록)
  ├── 통계 (최근 6개월 월별 차트 + 난이도별 완등률)
  └── 프로필 (이번달 통계 + 기록 피드 + 로그아웃)

✅ Phase 5-2 - 백엔드 추가 기능
  ├── 운동 기록 수정 API (PUT /api/records/{id})
  ├── FCM 토큰 등록/삭제 API (POST/DELETE /api/user/fcm-token)
  └── FCM 푸시 알림 인프라 (stub — Firebase 연결 시 활성화)
      └── 세팅일정 등록 시 즐겨찾기 사용자에게 알림 발송 예정

✅ Phase 5-3 - 관리자 웹 개선
  ├── AdminDashboard (ADMIN 전용): 지점 수 / 대기중 신청 통계 + 빠른 처리
  └── ManagerDashboard 강화: 직원현황 + 이벤트 미리보기 + 바로가기 링크

🔲 Phase 6 - SNS 기능 (완전 보류)
  ├── 클라이밍 포스트 (텍스트 + 이미지 + YouTube URL)
  ├── 공개범위 설정 (전체공개 / 나만)
  └── 미디어 스토리지: Cloudflare R2 또는 Supabase Storage Pro 검토 중
      └── 영상: YouTube URL 임베드 방식 유력 (직접 저장 비용 이슈)
```

---

*최초 작성: 2026-04-15 | 최종 수정: 2026-04-17*
