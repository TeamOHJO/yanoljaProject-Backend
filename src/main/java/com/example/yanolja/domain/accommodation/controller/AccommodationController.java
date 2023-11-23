package com.example.yanolja.domain.accommodation.controller;

import com.example.yanolja.domain.accommodation.dto.AccommodationFindResponse;
import com.example.yanolja.domain.accommodation.dto.RoomsFindResponse;
import com.example.yanolja.domain.accommodation.service.AccommodationService;
import com.example.yanolja.global.util.ResponseDTO;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accomodation")
public class AccommodationController {

    private AccommodationService accommodationService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<AccommodationFindResponse>>> getAllAccommodation(){
        List<AccommodationFindResponse> accomodations = accommodationService.getAllAccommodation();

        ResponseDTO<List<AccommodationFindResponse>> response = ResponseDTO.<List<AccommodationFindResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("숙박 전체 조회 성공")
            .data(accomodations)
            .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{accommodation_Id}")
    public ResponseEntity<ResponseDTO<AccommodationFindResponse>> getAccommodationById(
            @PathVariable(name = "accommodation_Id") Long accommodationId
    ){
        AccommodationFindResponse accommodation = accommodationService.getAccommodationById(accommodationId);

        ResponseDTO<AccommodationFindResponse> response = ResponseDTO.<AccommodationFindResponse>builder()
            .code(HttpStatus.OK.value())
            .message("ID로 숙박 조회 성공")
            .data(accommodation)
            .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{accommodation_Id}/{roomid}")
    public ResponseEntity<ResponseDTO<List<RoomsFindResponse>>> getRoomsByAccommodationId(
        @PathVariable(name = "accommodation_Id") Long accommodationId) {
        List<RoomsFindResponse> rooms = accommodationService.getRoomsByAccommodationId(accommodationId);

        ResponseDTO<List<RoomsFindResponse>> response = ResponseDTO.<List<RoomsFindResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("숙박ID로 방 전체 조회 성공")
            .data(rooms)
            .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<List<AccommodationFindResponse>>> searchAccommodations(
        @RequestParam(required = false) List<String> categories,
        @RequestParam(required = false) Boolean isDomestic,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) Integer numberOfPersons) {

        List<AccommodationFindResponse> accommodations = accommodationService.searchAccommodationsWithFilters(categories, isDomestic, startDate, endDate, numberOfPersons);

        ResponseDTO<List<AccommodationFindResponse>> response = ResponseDTO.<List<AccommodationFindResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("필터링 된 숙박 검색 결과 조회 성공")
            .data(accommodations)
            .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{accommodationId}/images")
    public ResponseEntity<ResponseDTO<List<String>>> getImagesByAccommodationId(@PathVariable Long accommodationId) {
        List<String> images = accommodationService.getImagesByAccommodationId(accommodationId);

        ResponseDTO<List<String>> response = ResponseDTO.<List<String>>builder()
            .code(HttpStatus.OK.value())
            .message("숙박 이미지 조회 성공")
            .data(images)
            .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{roomId}/images")
    public ResponseEntity<ResponseDTO<List<String>>> getImageByRoomId(@PathVariable Long roomId){
        List<String> images = accommodationService.getImagesByRoomId(roomId);

        ResponseDTO<List<String>> response = ResponseDTO.<List<String>>builder()
            .code(HttpStatus.OK.value())
            .message("방 이미지 조회 성공")
            .data(images)
            .build();
        return ResponseEntity.ok(response);
    }


}
