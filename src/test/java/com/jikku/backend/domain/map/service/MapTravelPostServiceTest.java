package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.MapTravelPostRequest;
import com.jikku.backend.domain.map.dto.MapTravelPostResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

// TravelPost 등록 기능이 아직 없어 실제 DB에 travel_post 행이 없다.
// 활성화하면 ENTITY_NOT_FOUND로 실패하고, 통과하더라도 공유 DB에 map_sticker 행이
// 커밋돼 재실행 시 중복으로 막힌다. 저장 검증은 다음 이슈에서 함께 다룬다.
@Disabled("TravelPost 등록 구현 전까지 검증 불가")
@Tag("integration")
@SpringBootTest
class MapTravelPostServiceTest {

  private static final Map<String, String> ENV = loadEnvFile();

  @Autowired
  private MapTravelPostService mapTravelPostService;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("DB_URL", () -> requireEnv("DB_URL"));
    registry.add("DB_USERNAME", () -> requireEnv("DB_USERNAME"));
    registry.add("DB_PASSWORD", () -> requireEnv("DB_PASSWORD"));
    registry.add("JWT_SECRET", () -> requireEnv("JWT_SECRET"));
    registry.add("TOUR_API_SERVICE_KEY", () -> requireEnv("TOUR_API_SERVICE_KEY"));
    registry.add("TOUR_API_MOBILE_APP", () -> requireEnv("TOUR_API_MOBILE_APP"));
  }

  @Test
  @DisplayName("읍면동 지도 사진 포스트 저장 API 수동 검증")
  void saveMapTravelPostTest() {
    Long memberId = 1L;
    Integer sigunguCd = 51110;

    MapTravelPostRequest request = MapTravelPostRequest.builder()
      .travelPostId(1L)
      .posX(0.7f)
      .posY(0.3f)
      .scale(1.0f)
      .zIndex(0)
      .build();

    MapTravelPostResponse response =
      mapTravelPostService.saveMapTravelPost(memberId, sigunguCd, request);

    System.out.println("===== 저장 결과 =====");
    System.out.println(response);
  }

  private static String requireEnv(String key) {
    String systemValue = System.getenv(key);
    if (systemValue != null && !systemValue.isBlank()) {
      return systemValue;
    }

    String envFileValue = ENV.get(key);
    if (envFileValue != null && !envFileValue.isBlank()) {
      return envFileValue;
    }

    throw new IllegalStateException(key + " 환경변수가 필요합니다.");
  }

  private static Map<String, String> loadEnvFile() {
    Map<String, String> values = new HashMap<>();
    Path envPath = Path.of(".env");

    if (!Files.exists(envPath)) {
      return values;
    }

    try {
      for (String line : Files.readAllLines(envPath)) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
          continue;
        }

        int separatorIndex = trimmed.indexOf('=');
        String key = trimmed.substring(0, separatorIndex).trim();
        String value = trimmed.substring(separatorIndex + 1).trim();
        values.put(key, value);
      }
    } catch (IOException e) {
      throw new IllegalStateException(".env 파일을 읽는 중 오류가 발생했습니다.", e);
    }

    return values;
  }
}
