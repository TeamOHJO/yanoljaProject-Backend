package com.example.yanolja.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ChangePasswordRequest {

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;


}
