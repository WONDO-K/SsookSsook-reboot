package com.stillalive.Ssook_BE.pay.dto;

import com.stillalive.Ssook_BE.enums.PayType;
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
    private int cardId;

    @NotNull(message = "식당 ID는 필수입니다.")
    private int dinerId;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 0, message = "결제 금액은 0원 이상이어야 합니다.")  // `Min`을 사용하여 0 이상의 정수만 허용
    private int amount;

    @NotNull(message = "주문한 메뉴는 필수입니다.")
    private List<PayDetailDto> payDetails; // 메뉴 ID와 수량 정보를 담은 리스트

    @NotNull(message = "결제 유형은 필수입니다.")
    private PayType payType; // 결제 유형

    // 내부 클래스 혹은 별도의 클래스로 PayDetailDto 정의
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayDetailDto {
        private Integer menuId;
        private Integer quantity;
    }
}