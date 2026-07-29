package com.jikku.backend.domain.map.repository;

import com.jikku.backend.domain.map.entity.MapSticker;
import com.jikku.backend.domain.map.enums.StickerType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapStickerRepository extends JpaRepository<MapSticker, Long> {
  List<MapSticker> findByMemberIdAndSigunguCd(Long memberId, Integer sigunguCd);

  List<MapSticker> findByMemberIdAndSigunguCdAndStickerType(
    Long memberId,
    Integer sigunguCd,
    StickerType stickerType
  );

  Optional<MapSticker> findByMemberIdAndSigunguCdAndStickerTypeAndTravelPostId(
    Long memberId,
    Integer sigunguCd,
    StickerType stickerType,
    Long travelPostId
  );
}
