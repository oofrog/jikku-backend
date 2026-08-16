# 1단계: 소스에서 실행 가능한 jar를 만든다
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 빌드 스크립트와 래퍼를 먼저 복사해 의존성 다운로드를 별도 레이어로 굳힌다.
# 소스만 바뀐 배포에서는 이 레이어가 캐시돼 빌드가 훨씬 빨라진다.
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --console=plain

COPY src src
# 테스트는 CI에서 이미 돌았고, 통합 테스트는 여기서 붙을 DB도 없다
RUN ./gradlew bootJar --no-daemon --console=plain

# 2단계: 실행에는 JRE만 있으면 된다 (이미지 크기 절감)
FROM eclipse-temurin:21-jre
WORKDIR /app

# root로 돌리지 않는다
RUN useradd --system --create-home --shell /usr/sbin/nologin jikku
USER jikku

COPY --from=builder --chown=jikku:jikku /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
