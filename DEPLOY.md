# 배포 가이드

서버는 **Fly.io**(도쿄 리전), DB는 **Supabase**, 이미지 저장은 **Cloudflare R2**를 쓴다.
Fly에는 앱 컨테이너 하나만 뜨고, DB·스토리지는 기존 그대로다.

## 자동화되어 있는 것

| 언제 | 무엇이 도는가 |
| --- | --- |
| PR을 올릴 때 | `.github/workflows/ci.yml` — 빌드 + 유닛 테스트 |
| `main`에 머지될 때 | `.github/workflows/deploy.yml` — 유닛 테스트 통과 후 Fly.io 배포 |

CI는 **유닛 테스트만** 돌린다. `@Tag("integration")`이 붙은 테스트는 운영 Supabase와 TourAPI에 실제로 붙기 때문에
CI에서 제외한다. 러너에 DB 접속 정보를 두지 않는 편이 안전하고, 접속하게 두면 CI가 돌 때마다 JPA가
운영 스키마를 건드리게 된다. 로컬에서 `./gradlew test`를 그냥 실행하면 통합 테스트까지 전부 돈다.

---

## 최초 1회 설정

배포는 아래 세 가지가 갖춰져야 동작한다. **아직 안 되어 있으면 배포 잡은 그냥 건너뛴다**
(`DEPLOY_ENABLED` 변수가 없으면 실행되지 않으므로, 셋업 전에도 CI는 정상적으로 돈다).

### 1. Fly.io 앱 만들기

```bash
brew install flyctl          # 또는 curl -L https://fly.io/install.sh | sh
fly auth login
fly apps create jikku-backend   # fly.toml의 app 이름과 같아야 한다
```

### 2. 환경변수를 Fly 시크릿으로 등록

컨테이너에는 `.env` 파일이 없다. 로컬에서 `.env`로 채우던 값들을 그대로 시크릿으로 넣어야 한다.

```bash
fly secrets set \
  DB_URL='...' DB_USERNAME='...' DB_PASSWORD='...' \
  TOUR_API_SERVICE_KEY='...' TOUR_API_MOBILE_APP='jikku' \
  JWT_SECRET='...' DEV_LOGIN_KEY='...' \
  R2_ACCOUNT_ID='...' R2_ACCESS_KEY_ID='...' R2_SECRET_ACCESS_KEY='...' \
  R2_BUCKET='...' R2_PUBLIC_BASE_URL='https://...' \
  CORS_ALLOWED_ORIGINS='http://localhost:3000,http://localhost:5173'
```

`.env.example`에 있는 항목이 전부 들어가야 한다. 하나라도 빠지면 앱이 뜨다가 죽는다.

**`CORS_ALLOWED_ORIGINS`는 배포 환경에서 특히 주의한다.** 로컬에서는 값이 없어도 `localhost:3000`·`5173`이
기본 허용이지만, Fly에 이 변수를 안 넣으면 그 기본값이 그대로 쓰여서 나중에 프론트를 배포했을 때 막힌다.
반대로 **프론트를 로컬에서 돌리고 백엔드만 배포해 연동하는 단계**에서는, 브라우저가 보내는 오리진이
`http://localhost:3000`이므로 Fly 시크릿에도 localhost가 들어 있어야 한다.

`fly secrets set`은 append가 아니라 **통째로 덮어쓰기**다. 하나만 추가하려 해도 유지할 오리진을 전부 나열해야 한다.
2026-08-28 기준 현재 값:

```bash
fly secrets set CORS_ALLOWED_ORIGINS='https://ji-kku-frontend-seven.vercel.app,http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173'
```

끝 슬래시를 붙이면 안 된다(`https://ji-kku-frontend-seven.vercel.app/` ✗). Spring이 Origin 헤더와 문자열을 그대로 비교한다.
적용 확인은 대시보드의 digest(값의 해시라 편집 불가)가 아니라 preflight를 직접 쏴서 한다:

```bash
curl -i -X OPTIONS https://jikku-backend.fly.dev/health \
  -H "Origin: https://ji-kku-frontend-seven.vercel.app" \
  -H "Access-Control-Request-Method: GET"
```

### 3. GitHub에 배포 토큰 등록

```bash
fly tokens create deploy -x 8760h   # 1년짜리 배포 전용 토큰
```

출력된 토큰을 저장소 **Settings → Secrets and variables → Actions** 에서:

- **Secrets** 탭 → `FLY_API_TOKEN` 에 토큰 값
- **Variables** 탭 → `DEPLOY_ENABLED` 를 `true` 로

`DEPLOY_ENABLED`가 `true`가 되는 순간부터 `main` 머지마다 자동 배포된다.

---

## 수동 배포

```bash
fly deploy            # 로컬에서 직접 (flyctl 로그인 상태여야 함)
```

GitHub Actions 탭의 **Deploy → Run workflow** 로도 코드 변경 없이 다시 배포할 수 있다.

## 상태 확인

```bash
fly status            # 머신이 떠 있는지
fly logs              # 실시간 로그 (기동 실패 원인은 대부분 여기서 보인다)
fly secrets list      # 등록된 시크릿 이름 (값은 안 보인다)
```

배포 후 주소는 `https://jikku-backend.fly.dev` 이고, Swagger는 `/swagger-ui/index.html` 이다.

---

## 설정 프로필

공통 설정은 `application.yml`에 있고, 환경별로 갈리는 값만 나눠 뒀다.

| 프로필 | 파일 | 차이 |
| --- | --- | --- |
| `local` (기본) | `application-local.yml` | SQL 로그 켬 |
| `prod` | `application-prod.yml` | SQL 로그 끔, 로깅 INFO |

프로필을 지정하지 않으면 `local`이라 `./gradlew bootRun`은 예전 그대로다. 배포본은 `fly.toml`의
`SPRING_PROFILES_ACTIVE = "prod"`로 켠다. 기동 로그 맨 앞에 `The following 1 profile is active: "prod"`가
찍히는지 보면 제대로 적용됐는지 알 수 있다.

`ddl-auto`는 아직 `update`다. 엔티티 작업이 끝나면 `validate`로 내리기로 했다(그러면 스키마는
Supabase에서만 관리하고 스프링은 대조만 한다). 바꿀 때는 main과 test의 `application.yml` **양쪽**을 고쳐야 한다.

---

## 알아둘 것

- **머신을 재우지 않는다.** 유휴 시 자동으로 끄면 다음 첫 요청이 콜드스타트로 수십 초 걸린다.
  시연·심사 중에 그러면 안 되므로 항상 1대를 켜 두도록 설정했다(`min_machines_running = 1`).
- **메모리는 1GB.** 512MB로 줄이면 기동 중에 죽을 수 있다.
- **다른 곳으로 옮길 수 있다.** 배포에 필요한 건 `Dockerfile` 하나이고 Fly 전용 설정은 `fly.toml`뿐이라,
  Railway·Render·직접 띄운 서버로 옮길 때 같은 이미지를 그대로 쓴다.
