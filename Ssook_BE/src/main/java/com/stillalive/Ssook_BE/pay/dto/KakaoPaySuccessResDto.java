package com.stillalive.Ssook_BE.pay.dto;

import lombok.Data;

@Data
public class KakaoPaySuccessResDto {
    private String transactionId; // 거래 ID
    private Integer amount;       // 결제 금액
    private Integer parentId;     // 부모 ID
}
