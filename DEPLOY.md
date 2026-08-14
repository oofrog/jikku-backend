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
  R2_BUCKET='...' R2_PUBLIC_BASE_URL='https://...'
```

`.env.example`에 있는 항목이 전부 들어가야 한다. 하나라도 빠지면 앱이 뜨다가 죽는다.

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

## 알아둘 것

- **머신을 재우지 않는다.** 유휴 시 자동으로 끄면 다음 첫 요청이 콜드스타트로 수십 초 걸린다.
  시연·심사 중에 그러면 안 되므로 항상 1대를 켜 두도록 설정했다(`min_machines_running = 1`).
- **메모리는 1GB.** 512MB로 줄이면 기동 중에 죽을 수 있다.
- **다른 곳으로 옮길 수 있다.** 배포에 필요한 건 `Dockerfile` 하나이고 Fly 전용 설정은 `fly.toml`뿐이라,
  Railway·Render·직접 띄운 서버로 옮길 때 같은 이미지를 그대로 쓴다.
