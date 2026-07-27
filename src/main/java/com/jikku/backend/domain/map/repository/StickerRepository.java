package com.jikku.backend.domain.map.repository;

import com.jikku.backend.domain.map.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
}
