package com.stillalive.Ssook_BE.pay.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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

    @NotBlank(message = "유효기간은 필수 입력 값입니다.")
    private String expirationDate; // mm/YY 형식의 문자열로 유효기간을 받음

    private boolean isActive = true;

    // 유효기간을 LocalDate로 변환하여 반환하는 메서드 추가
    @JsonIgnore
    public YearMonth getExpirationAsYearMonth() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return YearMonth.parse(this.expirationDate, formatter);
    }
}