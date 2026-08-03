package com.jikku.backend.domain.travelPost.repository;

import com.jikku.backend.domain.travelPost.entity.TravelPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPostRepository extends JpaRepository<TravelPost, Long> {
}
