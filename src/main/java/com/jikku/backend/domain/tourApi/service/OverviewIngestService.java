package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.client.TourApiClient;
import com.jikku.backend.domain.tourApi.dto.DetailItem;
import com.jikku.backend.domain.tourApi.dto.TourApiResponse;
import com.jikku.backend.domain.tourApi.repository.FestivalRepository;
import com.jikku.backend.domain.tourApi.repository.OverviewRepository;
import com.jikku.backend.domain.tourApi.repository.SpotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * detailCommon2로 관광지·축제 상세 설명(overview)을 채운다.
 * 목록과 달리 건당 1회 호출이 들어 하루 한도(1,000건) 안에 전량을 못 채우므로,
 * 이미 채워진 건 건너뛰고 여러 번 나눠 실행하는 걸 전제로 만들었다.
 */
@Slf4j
@Service
public class OverviewIngestService {

    private static final long CALL_INTERVAL_MS = 300;

    private final TourApiClient tourApiClient;
    private final SpotRepository spotRepository;
    private final FestivalRepository festivalRepository;

    public OverviewIngestService(TourApiClient tourApiClient,
                                 SpotRepository spotRepository,
                                 FestivalRepository festivalRepository) {
        this.tourApiClient = tourApiClient;
        this.spotRepository = spotRepository;
        this.festivalRepository = festivalRepository;
    }

    /**
     * 남은 호출 한도만큼 overview를 채운다. 축제를 먼저 끝내고 관광지로 넘어간다.
     *
     * @param maxCalls 이번 실행에서 쓸 TourAPI 호출 상한
     * @return 실제로 채운 건수
     */
    public int ingest(int maxCalls) {
        int remaining = spotRepository.countWithoutOverview() + festivalRepository.countWithoutOverview();
        log.info("overview 적재 시작: 미적재 {}건, 이번 실행 한도 {}건", remaining, maxCalls);

        int filled = fill(festivalRepository, "축제", maxCalls);
        filled += fill(spotRepository, "관광지", maxCalls - filled);

        log.info("===== overview 적재 완료: {}건 채움. 남은 미적재 관광지 {}건 / 축제 {}건 =====",
                filled, spotRepository.countWithoutOverview(), festivalRepository.countWithoutOverview());
        return filled;
    }

    private int fill(OverviewRepository repository, String label, int budget) {
        if (budget <= 0) {
            return 0;
        }

        List<Long> targets = repository.findContentIdsWithoutOverview(budget);
        int filled = 0;

        for (Long contentId : targets) {
            try {
                TourApiResponse<DetailItem> response = tourApiClient.getDetail(contentId);
                List<DetailItem> items = response.items();

                if (items.isEmpty()) {
                    log.warn("{} contentId={} 상세 응답 없음 (다음 실행에서 재시도)", label, contentId);
                    continue;
                }

                // 조회는 됐는데 내용이 없는 경우가 있다. 빈 문자열로 남겨 다음 실행이 같은 건을
                // 다시 호출하지 않게 한다. 호출 한도가 빠듯해 재시도 낭비를 피해야 한다.
                String overview = items.getFirst().overview();
                repository.updateOverview(contentId, overview == null ? "" : overview);
                filled++;

                Thread.sleep(CALL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("{} overview 적재 중단됨 ({}건까지 진행)", label, filled);
                break;
            } catch (Exception e) {
                // 한 건 실패로 전체를 멈추지 않는다. overview가 NULL로 남아 다음 실행이 이어받는다.
                log.error("{} contentId={} 실패 (건너뜀): {}", label, contentId, e.getMessage());
            }

            if (filled > 0 && filled % 100 == 0) {
                log.info("{} 진행 {}/{}건", label, filled, targets.size());
            }
        }

        log.info("{} overview {}건 채움 (대상 {}건)", label, filled, targets.size());
        return filled;
    }
}
