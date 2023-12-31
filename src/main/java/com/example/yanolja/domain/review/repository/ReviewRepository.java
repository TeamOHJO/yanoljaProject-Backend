package com.example.yanolja.domain.review.repository;

import com.example.yanolja.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {


    // 숙박업체Id를 기반으로 리뷰 전체 조회
    @Query("SELECT r FROM Review r WHERE r.room.accommodation.accommodationId = :accommodationId")
    List<Review> findByAccommodationId(@Param("accommodationId") Long accommodationId);

    // 방Id를 기반으로 리뷰 전체 조회
    List<Review> findByRoom_RoomId(Long roomId);

    Optional<Review> findByReviewIdAndUserId(Long reviewId, Long userId);

    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId")
    List<Review> findByUserId(@Param("userId") Long userId);

}
