package com.example.yanolja.domain.basket.dto;

import com.example.yanolja.domain.accommodation.entity.AccommodationRooms;
import com.example.yanolja.domain.reservation.entity.Reservations;
import com.example.yanolja.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateBasketResponse(
    @NotNull String email,
    @NotNull String name,
    @NotNull String phonenumber,
    @NotNull Long AccomodationRoomId,
    @NotNull int price,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    @NotNull int numberOfPerson
) {

    public static CreateBasketResponse fromEntity(
        User user, AccommodationRooms rooms, Reservations reservations) {
        return new CreateBasketResponse(
            user.getEmail(),
            user.getUsername(),
            user.getPhonenumber(),
            rooms.getRoomId(),
            rooms.getPrice(),
            reservations.getStartDate(),
            reservations.getEndDate(),
            reservations.getNumberOfPerson()
        );
    }
}
