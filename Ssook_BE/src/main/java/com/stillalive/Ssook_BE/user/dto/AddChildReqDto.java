package com.stillalive.Ssook_BE.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddChildReqDto {

    @NotBlank(message = "자녀 전화번호는 필수 입력 값입니다.")
    private String tel;
}
