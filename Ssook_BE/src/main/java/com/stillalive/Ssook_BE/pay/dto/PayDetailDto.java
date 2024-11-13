package com.stillalive.Ssook_BE.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayDetailDto {
    private String menuName; // 메뉴 이름
    private Integer quantity; // 메뉴 주문 수량
    private Integer price; // 메뉴 개별 가격
}