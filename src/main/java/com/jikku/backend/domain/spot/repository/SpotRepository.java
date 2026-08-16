package com.jikku.backend.domain.spot.repository;

import com.jikku.backend.domain.spot.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}
