package com.jikku.backend.domain.travelPost.service;

import com.jikku.backend.domain.travelPost.dto.TravelPostDetailResponse;
import com.jikku.backend.domain.travelPost.entity.TravelPost;
import com.jikku.backend.domain.travelPost.repository.TravelPostRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPostService {

  private final TravelPostRepository travelPostRepository;

  public TravelPostDetailResponse getTravelPostDetail(Long travelPostId) {
    TravelPost travelPost = travelPostRepository.findById(travelPostId)
      .orElseThrow(() -> new BaseException(
        GeneralErrorCode.ENTITY_NOT_FOUND,
        "존재하지 않는 여행기록입니다."
      ));

    return TravelPostDetailResponse.from(travelPost);
  }
}
