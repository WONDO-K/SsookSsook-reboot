package com.stillalive.Ssook_BE.pay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReqDto {

    @NotNull(message = "카드 ID는 필수입니다.")
    private Long cardId;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 0, message = "결제 금액은 0원 이상이어야 합니다.")  // `Min`을 사용하여 0 이상의 정수만 허용
    private int amount;

    @NotNull(message = "주문한 메뉴는 필수입니다.")
    private List<String> menuNames;  // 주문한 메뉴 이름 리스트
}