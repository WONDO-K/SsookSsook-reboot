package com.stillalive.Ssook_BE.pay.dto;

import com.stillalive.Ssook_BE.enums.PayType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class NfcPaymentReqDto implements PaymentRequest{

    @NotNull(message = "학생 ID는 필수입니다.")
    private int childId;

    @NotNull(message = "식당 ID는 필수입니다.")
    private int dinerId;

    @NotNull(message = "총 결제 금액은 필수입니다.")
    @Min(value = 0, message = "총 결제 금액은 0원 이상이어야 합니다.")  // `Min`을 사용하여 0 이상의 정수만 허용
    private int amount;

    @NotNull(message = "주문한 메뉴는 필수입니다.")
    private List<PayDetailDto> payDetails; // 메뉴 ID와 수량 정보를 담은 리스트

    @Builder.Default
    private PayType payType = PayType.PAYMENT; // 결제 유형

}