package com.stillalive.Ssook_BE.pay.dto;

import com.example.demo.entity.Balance;
import com.example.demo.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCardResDto {

        private Long childId;

        private Long cardId;

        private int balance;

        private String cardToken;

        private String expirationDate;

        private boolean isActive;

        public static MyCardResDto toDto(Long childId, Card card, Balance balance) {
                return MyCardResDto.builder()
                        .childId(childId)
                        .cardId(card.getCardId())
                        .balance(balance.getCurrentBalance())
                        .cardToken(card.getCardToken())
                        .expirationDate(card.getExpirationDate().toString())
                        .isActive(card.isActive())
                        .build();
        }
}
