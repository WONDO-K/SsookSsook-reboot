package com.stillalive.Ssook_BE.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargePointReqDto {

    private String impUid; // 포트원 결제 고유 ID (imp_uid) 차후 검증 로직에도 사용 가능
    private int userId; // 로그인 ID 또는 유저의 고유 식별자
    private int amount;    // 충전할 포인트 금액
}