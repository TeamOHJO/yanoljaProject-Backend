package com.example.yanolja.domain.user.controller;

import com.example.yanolja.domain.user.dto.ChangePasswordRequest;
import com.example.yanolja.domain.user.dto.CreateUserRequest;
import com.example.yanolja.domain.user.dto.CreateUserResponse;
import com.example.yanolja.domain.user.dto.TestUserResponse;
import com.example.yanolja.domain.user.dto.UpdateUserRequest;
import com.example.yanolja.domain.user.entity.User;
import com.example.yanolja.domain.user.service.UserService;
import com.example.yanolja.global.springsecurity.PrincipalDetails;
import com.example.yanolja.global.util.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<?>> signup(
        @Valid @RequestBody CreateUserRequest createUserRequest) {
        ResponseDTO<?> response = userService.signup(createUserRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseDTO<?>> deleteUser(
        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        ResponseDTO<?> response = userService.deleteUser(principalDetails.getUser().getId());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/mypage")
    public ResponseEntity<ResponseDTO<CreateUserResponse>> getMyPageInfo(
        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        CreateUserResponse userInfo = CreateUserResponse.fromEntity(user);
        ResponseDTO<CreateUserResponse> response = ResponseDTO.res(
            HttpStatus.OK, "사용자 정보 조회 성공", userInfo);
        return ResponseEntity.ok(response);
    }


    @PutMapping("")
    public ResponseEntity<ResponseDTO<?>> updateUserInfo(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @Valid @RequestBody UpdateUserRequest updateUserRequest) {

        ResponseDTO<?> response = userService.updateUser(principalDetails.getUser().getId(),
            updateUserRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/password")
    public ResponseEntity<ResponseDTO<?>> changePassword(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody ChangePasswordRequest changePasswordRequest) {

        ResponseDTO<?> response = userService.changePassword(principalDetails.getUser().getId(),
            changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // Authenticated user 샘플테스트 코드입니다
    @GetMapping("/test")
    public ResponseEntity<?> test(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();

        TestUserResponse testUserResponse = TestUserResponse.fromEntity(user);
        return ResponseEntity.ok(testUserResponse);
    }
}
