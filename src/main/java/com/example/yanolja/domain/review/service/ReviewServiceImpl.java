package com.example.yanolja.domain.review.service;

import com.example.yanolja.domain.reservation.entity.Reservations;
import com.example.yanolja.domain.reservation.exception.InvalidReservationException;
import com.example.yanolja.domain.reservation.repository.ReservationRepository;
import com.example.yanolja.domain.review.dto.AccommodationReviewResponse;
import com.example.yanolja.domain.review.dto.CreateReviewResponse;
import com.example.yanolja.domain.review.dto.CreateReviewRequest;
import com.example.yanolja.domain.review.dto.UpdateReviewRequest;
import com.example.yanolja.domain.review.dto.UserReviewResponse;
import com.example.yanolja.domain.review.entity.Review;
import com.example.yanolja.domain.review.entity.ReviewImages;
import com.example.yanolja.domain.review.exception.PermissionDeniedException;
import com.example.yanolja.domain.review.exception.ReviewNotFoundException;
import com.example.yanolja.domain.review.repository.ReviewImageRepository;
import com.example.yanolja.domain.review.repository.ReviewRepository;
import com.example.yanolja.domain.user.entity.User;
import com.example.yanolja.global.exception.ErrorCode;
import com.example.yanolja.global.util.ResponseDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Override
    public ResponseDTO<?> createReview(User user, Long reservationId, CreateReviewRequest request) {
        Reservations reservation = getValidReservation(reservationId, user);
        Review review = createReviewEntity(request, reservation, user);
        List<String> imageUrls = saveAndFetchImageUrls(request.images(), review);

        return createReviewResponse(review, imageUrls);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AccommodationReviewResponse> getReviewsByAccommodationId(Long accommodationId) {
        List<Review> reviews = reviewRepository.findByAccommodationId(accommodationId);
        return reviews.stream()
            .map(this::createAccommodationReviewResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(User user, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException());

        reviewRepository.findByReviewIdAndUserId(reviewId, user.getUserId())
            .orElseThrow(() -> new PermissionDeniedException());

        reviewImageRepository.deleteAll(
            reviewImageRepository.findAllByReviewReviewId(review.getReviewId()));
        reviewRepository.delete(review);
    }

    @Override
    public ResponseDTO<?> editReview(User user, Long reviewId, UpdateReviewRequest request) {
        Review review = getExistingReview(reviewId);

        updateReviewContentAndImages(review, request);
        Review updatedReview = reviewRepository.save(review);

        return updateReviewResponse(updatedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDTO<List<UserReviewResponse>> getUserReviews(User user) {
        List<Review> reviews = reviewRepository.findByUserId(user.getId());

        if (reviews.isEmpty()) {
            return ResponseDTO.res(HttpStatus.OK, "작성한 리뷰가 없습니다.", new ArrayList<>());
        }
        List<UserReviewResponse> reviewDTOs = reviews.stream()
            .map(UserReviewResponse::fromReview)
            .collect(Collectors.toList());
        return ResponseDTO.res(HttpStatus.OK, "리뷰 조회 성공", reviewDTOs);
    }

    private Reservations getValidReservation(Long reservationId, User user) {
        Reservations reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new InvalidReservationException(ErrorCode.INVALID_RESERVATION_ID));
        reservationRepository.checkReviewPermission(reservationId, user.getUserId())
            .orElseThrow(() -> new PermissionDeniedException());
        return reservation;
    }


    private Review createReviewEntity(CreateReviewRequest request, Reservations reservation,
        User user) {
        Review review = request.toEntity(reservation.getRoom(), user, reservation,
            reservation.getRoom().getAccommodation());
        return reviewRepository.save(review);
    }


    private List<String> saveAndFetchImageUrls(List<String> images, Review review) {
        images.forEach(image -> reviewImageRepository.save(ReviewImages.builder()
            .review(review)
            .image(image)
            .build()));
        return images;
    }

    private ResponseDTO<?> createReviewResponse(Review review, List<String> imageUrls) {

        CreateReviewResponse reviewResponse = CreateReviewResponse.fromReview(review, imageUrls);

        return ResponseDTO.res(HttpStatus.CREATED, "리뷰 작성 성공", reviewResponse);
    }

    private Review getExistingReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException());
    }

    private void updateReviewContentAndImages(Review review, UpdateReviewRequest request) {
        review.editReview(request.getReviewContent(), request.getStar());

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            reviewImageRepository.deleteAll(
                reviewImageRepository.findAllByReviewReviewId(review.getReviewId()));
            reviewImageRepository.save(new ReviewImages(review, request.getImage()));
        }
    }

    private ResponseDTO<?> updateReviewResponse(Review review) {
        List<String> imageUrls = review.getReviewImages().stream()
            .map(ReviewImages::getImage)
            .collect(Collectors.toList());
        CreateReviewResponse reviewResponse = CreateReviewResponse.fromReview(review, imageUrls);

        return ResponseDTO.res(HttpStatus.OK, "리뷰 수정 성공", reviewResponse);
    }

    private AccommodationReviewResponse createAccommodationReviewResponse(Review review) {
        List<String> images = review.getReviewImages().stream()
            .map(ReviewImages::getImage)
            .collect(Collectors.toList());

        return new AccommodationReviewResponse(
            review.getReviewId(),
            review.getRoom().getRoomId(),
            review.getUser().getUsername(),
            review.getRoom().getAccommodation().getAccommodationName(),
            review.getRoom().getName(),
            review.getRoom().getAccommodation().getCategory(),
            review.getReviewContent(),
            review.getStar(),
            images,
            review.getUpdatedAt()
        );
    }


}
