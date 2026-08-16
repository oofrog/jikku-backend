package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.client.TourApiClient;
import com.jikku.backend.domain.tourApi.dto.SpotItem;
import com.jikku.backend.domain.tourApi.dto.TourApiResponse;
import com.jikku.backend.domain.tourApi.repository.SpotIngestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class SpotIngestService {

    private static final long CALL_INTERVAL_MS = 300;

    private final TourApiClient tourApiClient;
    private final SpotIngestRepository spotRepository;

    public SpotIngestService(TourApiClient tourApiClient, SpotIngestRepository spotRepository) {
        this.tourApiClient = tourApiClient;
        this.spotRepository = spotRepository;
    }

    /**
     * 강원 관광지 전체를 페이지 단위로 순회하며 적재한다.
     * content_id 기준 upsert라 재실행해도 멱등하며, 실패한 페이지는 다음 실행 때 복구된다.
     *
     * @param numOfRows 한 페이지 크기 (최대 1000)
     * @return 저장한 건수
     */
    public int ingestAll(int numOfRows) {
        TourApiResponse<SpotItem> firstPage = tourApiClient.getSpots(1, numOfRows);
        int totalCount = firstPage.totalCount();

        if (totalCount == 0) {
            log.warn("관광지 목록이 0건이다. 응답 파싱 실패이거나 지역 파라미터가 잘못됐을 수 있다");
            return 0;
        }

        int totalPages = (totalCount + numOfRows - 1) / numOfRows;
        log.info("관광지 적재 시작: 총 {}건, {}페이지", totalCount, totalPages);

        int saved = 0;
        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
            try {
                // 1페이지는 총건수를 얻으려 이미 받았으므로 다시 호출하지 않는다
                TourApiResponse<SpotItem> page =
                        (pageNo == 1) ? firstPage : tourApiClient.getSpots(pageNo, numOfRows);

                saved += save(page.items());

                if (pageNo < totalPages) {
                    Thread.sleep(CALL_INTERVAL_MS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("관광지 적재 중단됨 ({}페이지까지 진행)", pageNo);
                break;
            } catch (Exception e) {
                log.error("관광지 {}페이지 실패 (건너뜀, 재실행 시 복구됨): {}", pageNo, e.getMessage());
            }
        }

        // 페이지 단위 catch가 DB 장애 같은 전면 실패까지 삼켜서 "0건 저장 완료"로 보이면
        // 빈 테이블을 그대로 넘기게 된다. 한 건도 못 넣었으면 실패로 드러낸다.
        if (saved == 0) {
            throw new IllegalStateException(
                    "관광지 적재 실패: 총 %d건 중 한 건도 저장되지 않았다".formatted(totalCount));
        }

        log.info("===== 관광지 적재 완료: {}건 저장 (총 {}건) =====", saved, totalCount);
        return saved;
    }

    private int save(List<SpotItem> items) {
        List<SpotItem> valid = items.stream().filter(this::isValid).toList();

        int skipped = items.size() - valid.size();
        if (skipped > 0) {
            log.warn("필수 필드 누락으로 관광지 {}건 제외", skipped);
        }

        spotRepository.saveAll(valid);
        return valid.size();
    }

    // 필수 필드가 비거나 코드가 숫자가 아니면 배치 전체가 롤백되므로 미리 걸러낸다.
    // 조용히 버리지 않도록 건별로 남긴다.
    private boolean isValid(SpotItem item) {
        boolean valid = item.hasNumericCodes() && StringUtils.hasText(item.title());

        if (!valid) {
            log.warn("관광지 항목 제외 (필수 필드 누락): contentid={}, title={}",
                    item.contentid(), item.title());
        }
        return valid;
    }
}
