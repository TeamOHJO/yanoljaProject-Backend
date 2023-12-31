package com.example.yanolja.domain.basket.service;

import com.example.yanolja.domain.accommodation.entity.AccommodationRooms;
import com.example.yanolja.domain.accommodation.repository.AccommodationRoomRepository;
import com.example.yanolja.domain.basket.dto.CreateBasketRequest;
import com.example.yanolja.domain.basket.dto.CreateBasketResponse;
import com.example.yanolja.domain.basket.dto.GetBasketResponse;
import com.example.yanolja.domain.basket.entity.Basket;
import com.example.yanolja.domain.basket.error.DuplicatedBasketException;
import com.example.yanolja.domain.basket.error.InvalidBasketIdException;
import com.example.yanolja.domain.basket.repository.BasketRepository;
import com.example.yanolja.domain.reservation.dto.CreateReservationRequest;
import com.example.yanolja.domain.reservation.entity.Reservations;
import com.example.yanolja.domain.reservation.exception.InvalidAccommodationRoomIdException;
import com.example.yanolja.domain.reservation.repository.ReservationRepository;
import com.example.yanolja.domain.review.repository.ReviewRepository;
import com.example.yanolja.domain.user.entity.User;
import com.example.yanolja.global.exception.ErrorCode;
import com.example.yanolja.global.util.ResponseDTO;
import com.example.yanolja.global.util.ReviewRatingUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {

    private final AccommodationRoomRepository accommodationRoomRepository;
    private final ReservationRepository reservationRepository;
    private final BasketRepository basketRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public ResponseDTO<CreateBasketResponse> addBasket(CreateBasketRequest createBasketRequest,
        User user,
        long roomsId) {

        Optional<AccommodationRooms> accommodationRooms =
            accommodationRoomRepository.findById(roomsId);

        //roomsId에 해당하는 방이 존재하지 않는 경우
        AccommodationRooms rooms = accommodationRooms.orElseThrow(
            () -> new InvalidAccommodationRoomIdException(
                ErrorCode.INVALID_ACCOMMODATION_ID));

        //장바구니에 같은 값을 다시 넣으려고 하는경우
        Optional<Reservations> checkDuplicate =
            reservationRepository.findByUserIdAndRoomRoomIdAndStartDateAndEndDateAndPaymentCompleted(
                user.getUserId(), roomsId, createBasketRequest.startDate(),
                createBasketRequest.endDate(), false);
        if (checkDuplicate.isPresent()) {
            throw new DuplicatedBasketException(ErrorCode.DUPLICATED_BASKET_CONTENT);
        }

        //장바구니에 추가는 reservation table에 paymentCompleted가 false로 기록되어야 한다.
        CreateReservationRequest createReservationRequest = new CreateReservationRequest(
            createBasketRequest.startDate(), createBasketRequest.endDate(),
            createBasketRequest.numberOfPerson()
        );

        Reservations reservations =
            reservationRepository.save(
                createReservationRequest.toEntity(user, rooms, false));

        basketRepository.save(
            createBasketRequest.toEntity(user, rooms, reservations));

        return ResponseDTO.res(HttpStatus.CREATED, "장바구니 추가 성공",
            CreateBasketResponse.fromEntity(user, rooms, reservations));
    }

    @Override
    public ResponseDTO<List<GetBasketResponse>> getBasket(User user) {

        //paymentCompleted가false인 항목은 장바구니에 있는 항목
        List<Reservations> reservations =
            reservationRepository.findAllByUserIdAndPaymentCompleted(user.getUserId(), false);

        List<GetBasketResponse> getBasketResponses = new ArrayList<>();

        for (Reservations reservationContent : reservations) {
            // 예약이 가능한지 체크
            boolean canReserve = checkConflictingReservations(reservationContent);

            getBasketResponses.add(GetBasketResponse.fromEntity(
                basketRepository.findByReservationReservationId(
                    reservationContent.getReservationId()),
                reservationContent.getRoom().getAccommodation(),
                reservationContent,
                reservationContent.getRoom(),
                reservationContent.getRoom().getRoomImages().get(0).getImage(),
                canReserve,
                ReviewRatingUtils.calculateAverageRating(reservationContent.getRoom()
                    .getAccommodation().getAccommodationId(), reviewRepository))
            );
        }

        return ResponseDTO.res(HttpStatus.CREATED, "장바구니 조회 성공",
            getBasketResponses);
    }

    @Override
    public ResponseDTO<Void> deleteBasket(User user, long basketId) {

        Basket basket = basketRepository.findById(basketId).orElseThrow(() -> {
            throw new InvalidBasketIdException(ErrorCode.INVALID_BASKET_ID);
        });

        reservationRepository.delete(basket.getReservation());
        basketRepository.delete(
            basketRepository.findByReservationReservationId(
                basket.getReservation().getReservationId())
        );

        return ResponseDTO.res(HttpStatus.NO_CONTENT, "장바구니 삭제 성공", null);
    }

    private boolean checkConflictingReservations(Reservations reservationContent) {
        Optional<Reservations> conflictingReservations =
            reservationRepository.findConflictingReservations(
                reservationContent.getRoom().getRoomId(),
                reservationContent.getStartDate(), reservationContent.getEndDate()
            );

        boolean canReserve = !reservationContent.getStartDate().isBefore(LocalDate.now());

        if (conflictingReservations.isPresent()) {
            canReserve = false;
        }

        return canReserve;
    }
}


