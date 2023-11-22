package com.example.yanolja.domain.accommodation.controller;

import com.example.yanolja.domain.accommodation.dto.AccommodationFindResponse;
import com.example.yanolja.domain.accommodation.dto.AccommodationListFindResponse;
import com.example.yanolja.domain.accommodation.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accomodation")
public class AccommodationController {

    private AccommodationService accommodationService;

    @GetMapping
    public ResponseEntity<List<AccommodationListFindResponse>> getAllAccomodation(){
        List<AccommodationListFindResponse> accomodations = accommodationService.getAllAccommodation();

        return ResponseEntity.ok(accomodations);
    }

    @GetMapping("/{accommodation_Id}")
    public ResponseEntity<AccommodationFindResponse> getAccomodationById(
            @PathVariable(name = "accommodation_Id") Long accommodationId
    ){
        AccommodationFindResponse accommodation = accommodationService.getAccommodationById(accommodationId);

        return ResponseEntity.ok(accommodation);
    }

    @GetMapping("/{accommoation_Id}/{roomid}")
    public ResponseEntity<AccommodationFindResponse> getAccomodatioroomsnById(
            @PathVariable(name = "accommodation_Id") Long accommodationId
    ){
        AccommodationFindResponse accommodation = accommodationService.getAccommodationById(accommodationId);

        return ResponseEntity.ok(accommodation);
    }

    @GetMapping("/보류") //todo 검색 URL
    public ResponseEntity<List<AccommodationListFindResponse>> searchAccommodationByName(
            @RequestParam("accommodation_Name") String accommodationName
    ){
        List<AccommodationListFindResponse> accommodations = accommodationService.searchAccommodation(accommodationName);

        return ResponseEntity.ok(accommodationName);
    }



}
