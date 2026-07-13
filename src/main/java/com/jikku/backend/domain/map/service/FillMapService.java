package com.jikku.backend.domain.map.service;

import com.jikku.backend.domain.map.dto.FillMapRequest;
import com.jikku.backend.domain.map.dto.FillMapResponse;
import com.jikku.backend.domain.map.entity.FillMap;
import com.jikku.backend.domain.map.repository.FillMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import com.jikku.backend.domain.map.entity.MapType;
import com.jikku.backend.domain.map.entity.FillType;

@Service
@RequiredArgsConstructor
public class FillMapService {

  private final FillMapRepository fillMapRepository;

  // 시군구 색칠 지도 조회
  public List<FillMapResponse> getSigunguFillMap(Long memberId) {
    return fillMapRepository.findByMemberId(memberId)
      .stream()
      .map(fillMap -> new FillMapResponse(
        fillMap.getFillMapId(),
        fillMap.getSigunguId(),
        fillMap.getEmdId(),
        fillMap.getMapType().name(),
        fillMap.getFillType().name(),
        fillMap.getColor(),
        fillMap.getImgUrl()
      ))
      .toList();
  }

  // 지도 채우기 저장
  public FillMapResponse saveFillMap(FillMapRequest request, Long memberId) {
    FillMap fillMap = FillMap.builder()
      .memberId(memberId)
      .sigunguId(request.sigunguId())
      .emdId(request.emdId())
      .mapType(MapType.valueOf(request.mapType()))
      .fillType(FillType.valueOf(request.fillType()))
      .color(request.color())
      .imgUrl(request.imgUrl())
      .build();

    FillMap saved = fillMapRepository.save(fillMap);

    return new FillMapResponse(
      saved.getFillMapId(),
      saved.getSigunguId(),
      saved.getEmdId(),
      saved.getMapType().name(),
      saved.getFillType().name(),
      saved.getColor(),
      saved.getImgUrl()
    );
  }
}
