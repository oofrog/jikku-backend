package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.client.TourApiClient;
import com.jikku.backend.domain.tourApi.dto.FestivalItem;
import com.jikku.backend.domain.tourApi.dto.TourApiResponse;
import com.jikku.backend.domain.tourApi.repository.FestivalIngestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class FestivalIngestService {

    private static final long CALL_INTERVAL_MS = 300;
    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final TourApiClient tourApiClient;
    private final FestivalIngestRepository festivalRepository;

    public FestivalIngestService(TourApiClient tourApiClient, FestivalIngestRepository festivalRepository) {
        this.tourApiClient = tourApiClient;
        this.festivalRepository = festivalRepository;
    }

    /**
     * 지정일 이후 시작하는 강원 축제를 적재한다. content_id 기준 upsert라 재실행해도 멱등하다.
     *
     * @param eventStartFrom 이 날짜 이후 시작하는 축제만
     * @param numOfRows      한 페이지 크기
     * @return 저장한 건수
     */
    public int ingestFrom(LocalDate eventStartFrom, int numOfRows) {
        String startYmd = eventStartFrom.format(YMD);

        TourApiResponse<FestivalItem> firstPage = tourApiClient.getFestivals(startYmd, 1, numOfRows);
        int totalCount = firstPage.totalCount();

        if (totalCount == 0) {
            log.warn("축제 목록이 0건이다 (기준일 {}). 응답 파싱 실패이거나 지역 파라미터가 잘못됐을 수 있다", startYmd);
            return 0;
        }

        int totalPages = (totalCount + numOfRows - 1) / numOfRows;
        log.info("축제 적재 시작: 기준일 {} 이후 총 {}건, {}페이지", startYmd, totalCount, totalPages);

        int saved = 0;
        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
            try {
                TourApiResponse<FestivalItem> page =
                        (pageNo == 1) ? firstPage : tourApiClient.getFestivals(startYmd, pageNo, numOfRows);

                saved += save(page.items());

                if (pageNo < totalPages) {
                    Thread.sleep(CALL_INTERVAL_MS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("축제 적재 중단됨 ({}페이지까지 진행)", pageNo);
                break;
            } catch (Exception e) {
                log.error("축제 {}페이지 실패 (건너뜀, 재실행 시 복구됨): {}", pageNo, e.getMessage());
            }
        }

        // 페이지 단위 catch가 DB 장애 같은 전면 실패까지 삼켜서 "0건 저장 완료"로 보이면
        // 빈 테이블을 그대로 넘기게 된다. 한 건도 못 넣었으면 실패로 드러낸다.
        if (saved == 0) {
            throw new IllegalStateException(
                    "축제 적재 실패: 총 %d건 중 한 건도 저장되지 않았다".formatted(totalCount));
        }

        log.info("===== 축제 적재 완료: {}건 저장 (총 {}건) =====", saved, totalCount);
        return saved;
    }

    private int save(List<FestivalItem> items) {
        List<FestivalItem> valid = items.stream().filter(this::isValid).toList();

        int skipped = items.size() - valid.size();
        if (skipped > 0) {
            log.warn("필수 필드 누락으로 축제 {}건 제외", skipped);
        }

        festivalRepository.saveAll(valid);
        return valid.size();
    }

    // event_start_date가 NOT NULL이라 비어 있으면 배치 전체가 롤백된다. 미리 걸러내고 건별로 남긴다.
    private boolean isValid(FestivalItem item) {
        boolean valid = StringUtils.hasText(item.contentid())
                && StringUtils.hasText(item.title())
                && StringUtils.hasText(item.lDongRegnCd())
                && StringUtils.hasText(item.lDongSignguCd())
                && StringUtils.hasText(item.eventstartdate());

        if (!valid) {
            log.warn("축제 항목 제외 (필수 필드 누락): contentid={}, title={}, eventstartdate={}",
                    item.contentid(), item.title(), item.eventstartdate());
        }
        return valid;
    }
}
