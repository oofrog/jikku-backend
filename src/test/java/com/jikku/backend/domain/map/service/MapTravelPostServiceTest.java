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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

//@Disabled("수동 확인용 테스트")
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
