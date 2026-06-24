package com.jikku.backend.domain.tourApi.service;

import com.jikku.backend.domain.tourApi.repository.RegionRepository;
import com.jikku.backend.domain.tourApi.client.TourApiClient;
import com.jikku.backend.domain.tourApi.dto.VisitorDDItem;
import com.jikku.backend.domain.tourApi.repository.VisitorDDRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
