package com.jikku.backend.domain.travelPost.service;

import com.jikku.backend.domain.map.dto.FillMapListResponse;
import com.jikku.backend.domain.region.entity.Emd;
import com.jikku.backend.domain.region.repository.EmdRepository;
import com.jikku.backend.domain.region.repository.SigunguRepository;
import com.jikku.backend.domain.travelPost.dto.TravelPostBlockCreateRequest;
import com.jikku.backend.domain.travelPost.dto.TravelPostCreateRequest;
import com.jikku.backend.domain.travelPost.dto.TravelPostCreateResponse;
import com.jikku.backend.domain.travelPost.dto.TravelPostDetailResponse;
import com.jikku.backend.domain.travelPost.dto.TravelPostResponse;
import com.jikku.backend.domain.travelPost.dto.TravelPostSigunguResponse;
import com.jikku.backend.domain.travelPost.entity.TravelPost;
import com.jikku.backend.domain.travelPost.entity.TravelPostBlock;
import com.jikku.backend.domain.travelPost.enums.BlockType;
import com.jikku.backend.domain.travelPost.repository.TravelPostBlockRepository;
import com.jikku.backend.domain.travelPost.repository.TravelPostRepository;
import com.jikku.backend.global.apiPayload.code.GeneralErrorCode;
import com.jikku.backend.global.exception.BaseException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPostService {

  private final TravelPostRepository travelPostRepository;
  private final SigunguRepository sigunguRepository;
  private final EmdRepository emdRepository;
  private final TravelPostBlockRepository travelPostBlockRepository;

  public TravelPostDetailResponse getTravelPostDetail(
    Long memberId,
    Long travelPostId
  ) {
    TravelPost travelPost = travelPostRepository
      .findByTravelPostIdAndMemberId(travelPostId, memberId)
      .orElseThrow(() -> new BaseException(
        GeneralErrorCode.ENTITY_NOT_FOUND,
        "존재하지 않는 여행기록입니다."
      ));

    return TravelPostDetailResponse.from(travelPost);
  }

  public FillMapListResponse<TravelPostResponse> getTravelPosts(
    Long memberId,
    Integer sigunguCd,
    LocalDate date
  ) {
    List<TravelPost> travelPosts = date == null
      ? travelPostRepository
      .findByMemberIdAndEmd_Sigungu_SigunguCdOrderByLogDateDescTravelPostIdDesc(
        memberId,
        sigunguCd
      )
      : travelPostRepository
      .findByMemberIdAndEmd_Sigungu_SigunguCdAndLogDateOrderByTravelPostIdDesc(
        memberId,
        sigunguCd,
        date
      );

    return FillMapListResponse.from(
      travelPosts.stream()
        .map(TravelPostResponse::from)
        .toList()
    );
  }

  public FillMapListResponse<TravelPostSigunguResponse> getSigunguList() {
    return FillMapListResponse.from(
      sigunguRepository.findAllByOrderBySigunguNmAsc()
        .stream()
        .map(TravelPostSigunguResponse::from)
        .toList()
    );
  }

  @Transactional
  public TravelPostCreateResponse createTravelPost(
    Long memberId,
    TravelPostCreateRequest request
  ) {
    Emd emd = emdRepository.findById(request.emdId())
      .orElseThrow(() -> new BaseException(
        GeneralErrorCode.ENTITY_NOT_FOUND,
        "존재하지 않는 읍면동입니다."
      ));

    validateBlocks(request.blocks());

    String firstImage = request.blocks()
      .stream()
      .filter(block -> block.blockType() == BlockType.IMAGE)
      .map(TravelPostBlockCreateRequest::imgUrl)
      .findFirst()
      .orElse(null);

    TravelPost travelPost = TravelPost.of(
      memberId,
      emd,
      request.logDate(),
      request.title(),
      firstImage
    );

    TravelPost savedTravelPost =
      travelPostRepository.save(travelPost);

    List<TravelPostBlock> blocks = new java.util.ArrayList<>();

    for (int i = 0; i < request.blocks().size(); i++) {
      TravelPostBlockCreateRequest block = request.blocks().get(i);

      blocks.add(TravelPostBlock.of(
        savedTravelPost,
        block.blockType(),
        i,
        normalizeBlank(block.textContent()),
        normalizeBlank(block.imgUrl())
      ));
    }

    travelPostBlockRepository.saveAll(blocks);

    return TravelPostCreateResponse.of(savedTravelPost);
  }

  private void validateBlocks(
    List<TravelPostBlockCreateRequest> blocks
  ) {
    for (TravelPostBlockCreateRequest block : blocks) {
      if (block.blockType() == BlockType.IMAGE) {
        if (
          block.imgUrl() == null
            || block.imgUrl().isBlank()
        ) {
          throw new BaseException(
            GeneralErrorCode.INVALID_INPUT_VALUE,
            "IMAGE 블록에는 imgUrl이 필요합니다."
          );
        }

        if (
          block.textContent() != null
            && !block.textContent().isBlank()
        ) {
          throw new BaseException(
            GeneralErrorCode.INVALID_INPUT_VALUE,
            "IMAGE 블록에는 textContent를 넣을 수 없습니다."
          );
        }
      }

      if (block.blockType() == BlockType.TEXT) {
        if (
          block.textContent() == null
            || block.textContent().isBlank()
        ) {
          throw new BaseException(
            GeneralErrorCode.INVALID_INPUT_VALUE,
            "TEXT 블록에는 textContent가 필요합니다."
          );
        }

        if (
          block.imgUrl() != null
            && !block.imgUrl().isBlank()
        ) {
          throw new BaseException(
            GeneralErrorCode.INVALID_INPUT_VALUE,
            "TEXT 블록에는 imgUrl을 넣을 수 없습니다."
          );
        }

      }
    }
  }
  private String normalizeBlank(String value) {
    return (value == null || value.isBlank()) ? null : value;
  }
}

