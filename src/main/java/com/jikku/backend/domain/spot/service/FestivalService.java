package com.jikku.backend.domain.spot.service;

import com.jikku.backend.domain.spot.dto.ContentListResponse;
import com.jikku.backend.domain.spot.dto.FestivalDetailResponse;
import com.jikku.backend.domain.spot.dto.FestivalSummaryResponse;
import com.jikku.backend.domain.spot.enums.SpotErrorCode;
import com.jikku.backend.domain.spot.repository.FestivalRepository;
import com.jikku.backend.global.exception.BaseException;
import com.jikku.backend.global.util.ServiceTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FestivalService {

    private final FestivalRepository festivalRepository;

    /** 아직 끝나지 않은 축제를 시작일 오름차순으로. 진행 중인 축제가 먼저 오고 그다음 임박한 순이 된다. */
    @Transactional(readOnly = true)
    public ContentListResponse<FestivalSummaryResponse> getFestivals() {
        return ContentListResponse.from(
                festivalRepository.findNotEndedOrderByStartDate(ServiceTime.today()).stream()
                        .map(FestivalSummaryResponse::from)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public FestivalDetailResponse getFestival(Long contentId) {
        return festivalRepository.findWithSigungu(contentId)
                .map(FestivalDetailResponse::from)
                .orElseThrow(() -> new BaseException(SpotErrorCode.FESTIVAL_NOT_FOUND));
    }
}
