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

    // 하루치: 호출 → 강원 필터 → 저장. (반환값 = 저장한 건수, 확인용)
    public int ingestOneDay(String baseYmd, int numOfRows) {
        // 1) 전국 데이터 호출
        List<VisitorDDItem> all = tourApiClient.getVisitorData(baseYmd, numOfRows);

        // 2) 강원 18개 코드 읽어서 필터
        Set<Integer> gangwon = regionRepository.findAllSigunguCodes();
        List<VisitorDDItem> filtered = all.stream()
                .filter(item -> gangwon.contains(Integer.parseInt(item.signguCode())))
                .toList();

        // 3) 저장
        visitorDDRepository.saveAll(filtered);

        log.info("[{}] 전국 {}건 중 강원 {}건 저장", baseYmd, all.size(), filtered.size());
        return filtered.size();
    }

    // 시작일~종료일을 하루씩 돌며 적재한다. (이미 받은 날은 건너뜀)
    public void ingestRange(LocalDate start, LocalDate end, int numOfRows) {
        DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyyMMdd");

        // (2) 이어받기: 이미 저장된 날짜는 호출조차 안 한다
        Set<LocalDate> saved = visitorDDRepository.findSavedDates();

        long totalDays = start.datesUntil(end.plusDays(1)).count();
        long processed = 0;
        long totalSaved = 0;
        int skipped = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            processed++;

            // 이미 받은 날은 스킵
            if (saved.contains(date)) {
                skipped++;
                continue;
            }

            try {
                int cnt = ingestOneDay(date.format(ymd), numOfRows);  // 기존 하루치 메서드 재사용
                totalSaved += cnt;

                // (1) 호출 간격: 포털에 점잖게. 0.3초 쉼
                Thread.sleep(300);

            } catch (Exception e) {
                // (3) 한 날 실패해도 멈추지 않기. 빠진 날은 다음 실행 때 이어받기로 자동 재시도됨
                log.error("[{}] 적재 실패 (건너뜀): {}", date, e.getMessage());
            }

            // (4) 진행 로그: 50일마다 한 번
            if (processed % 50 == 0) {
                log.info("진행 {}/{}일 (스킵 {}, 누적 {}건)", processed, totalDays, skipped, totalSaved);
            }
        }

        log.info("===== 적재 완료: 총 {}일 중 스킵 {}일, 누적 {}건 =====", totalDays, skipped, totalSaved);
    }
}
