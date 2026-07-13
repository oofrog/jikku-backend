package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.repository.RegionRepository;
import com.jikku.backend.domain.tourApi.client.TourApiClient;
import com.jikku.backend.domain.tourApi.dto.VisitorDDItem;
import com.jikku.backend.domain.tourApi.repository.VisitorDDRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class VisitorDDService {

    private final TourApiClient tourApiClient;
    private final RegionRepository regionRepository;
    private final VisitorDDRepository visitorDDRepository;

    public VisitorDDService(TourApiClient tourApiClient,
                                RegionRepository regionRepository,
                                VisitorDDRepository visitorDDRepository) {
        this.tourApiClient = tourApiClient;
        this.regionRepository = regionRepository;
        this.visitorDDRepository = visitorDDRepository;
    }

    // 하루치: 호출 → 강원 필터 → 저장. 반환값은 저장 건수.
    public int ingestOneDay(String baseYmd, int numOfRows) {
        List<VisitorDDItem> all = tourApiClient.getVisitorData(baseYmd, numOfRows);

        Set<Integer> gangwon = regionRepository.findAllSigunguCodes();
        List<VisitorDDItem> filtered = all.stream()
                .filter(item -> gangwon.contains(Integer.parseInt(item.signguCode())))
                .toList();

        visitorDDRepository.saveAll(filtered);

        log.info("[{}] 전국 {}건 중 강원 {}건 저장", baseYmd, all.size(), filtered.size());
        return filtered.size();
    }

    // 시작일~종료일을 하루씩 돌며 적재한다. 이미 저장된 날은 호출조차 하지 않는다.
    public void ingestRange(LocalDate start, LocalDate end, int numOfRows) {
        DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyyMMdd");

        Set<LocalDate> saved = visitorDDRepository.findSavedDates();

        long totalDays = start.datesUntil(end.plusDays(1)).count();
        long processed = 0;
        long totalSaved = 0;
        int skipped = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            processed++;

            if (saved.contains(date)) {
                skipped++;
                continue;
            }

            try {
                int cnt = ingestOneDay(date.format(ymd), numOfRows);
                totalSaved += cnt;

                Thread.sleep(300); // 포털 호출 간격 조절

            } catch (Exception e) {
                // 한 날 실패해도 멈추지 않는다. 빠진 날은 다음 실행 때 이어받기로 재시도된다.
                log.error("[{}] 적재 실패 (건너뜀): {}", date, e.getMessage());
            }

            if (processed % 50 == 0) {
                log.info("진행 {}/{}일 (스킵 {}, 누적 {}건)", processed, totalDays, skipped, totalSaved);
            }
        }

        log.info("===== 적재 완료: 총 {}일 중 스킵 {}일, 누적 {}건 =====", totalDays, skipped, totalSaved);
    }
}
