package com.stillalive.Ssook_BE.pay.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCardReqDto {

    @NotBlank(message = "카드 번호는 필수 입력 값입니다.")
    private String cardNumber; // JWT로 토큰화할 카드 번호

    @NotBlank(message = "CVC 코드는 필수 입력 값입니다.")
    private String cvcCode; // JWT로 토큰화할 CVC 코드

    @NotNull(message = "만료일은 필수 입력 값입니다.")
    private Date expirationDate;

    private boolean isActive = true;
}