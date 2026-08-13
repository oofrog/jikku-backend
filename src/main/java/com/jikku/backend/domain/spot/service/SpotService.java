package com.jikku.backend.domain.spot.service;

import com.jikku.backend.domain.spot.dto.ContentListResponse;
import com.jikku.backend.domain.spot.dto.SpotDetailResponse;
import com.jikku.backend.domain.spot.dto.SpotSummaryResponse;
import com.jikku.backend.domain.spot.entity.Spot;
import com.jikku.backend.domain.spot.enums.SpotErrorCode;
import com.jikku.backend.domain.spot.repository.SpotRepository;
import com.jikku.backend.global.exception.BaseException;
import com.jikku.backend.global.util.ServiceTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotService {

    private static final int TODAY_COUNT = 10;

    private final SpotRepository spotRepository;

    /**
     * 오늘의 관광지 추천. 날짜를 시드로 써서 같은 날에는 몇 번을 호출해도 결과가 같다.
     * 뽑기를 DB가 아니라 자바에서 하는 이유는 Postgres의 setseed()가 세션 상태에 의존해
     * 커넥션 풀에서는 같은 결과를 보장하기 어렵기 때문이다.
     */
    @Transactional(readOnly = true)
    public ContentListResponse<SpotSummaryResponse> getTodaySpots() {
        List<Long> candidates = new ArrayList<>(spotRepository.findContentIdsWithImage());
        Collections.shuffle(candidates, new Random(ServiceTime.today().toEpochDay()));

        List<Long> picked = candidates.subList(0, Math.min(TODAY_COUNT, candidates.size()));

        // IN 조회는 순서를 보장하지 않아 뽑은 순서대로 다시 세운다. 그래야 날짜가 같으면 배열 순서까지 같다.
        Map<Long, Spot> spotsById = spotRepository.findAllWithSigungu(picked).stream()
                .collect(Collectors.toMap(Spot::getContentId, Function.identity()));

        return ContentListResponse.from(
                picked.stream()
                        .map(spotsById::get)
                        .filter(Objects::nonNull)
                        .map(SpotSummaryResponse::from)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public SpotDetailResponse getSpot(Long contentId) {
        return spotRepository.findWithSigungu(contentId)
                .map(SpotDetailResponse::from)
                .orElseThrow(() -> new BaseException(SpotErrorCode.SPOT_NOT_FOUND));
    }
}
