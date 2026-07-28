package com.jikku.backend.domain.map.repository;

import com.jikku.backend.domain.map.entity.MapSticker;
import com.jikku.backend.domain.map.enums.StickerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapStickerRepository extends JpaRepository<MapSticker, Long> {
  List<MapSticker> findByMemberIdAndSigunguCd(Long memberId, Integer sigunguCd);

  List<MapSticker> findByMemberIdAndSigunguCdAndStickerType(
    Long memberId,
    Integer sigunguCd,
    StickerType stickerType
  );
}
