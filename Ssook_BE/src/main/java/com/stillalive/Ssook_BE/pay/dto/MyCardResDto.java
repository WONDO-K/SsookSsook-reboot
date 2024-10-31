package com.stillalive.Ssook_BE.pay.dto;

import com.stillalive.Ssook_BE.domain.Balance;
import com.stillalive.Ssook_BE.domain.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCardResDto {

        private int childId;

        private int cardId;

        private int balance;

        private String cardToken;

        private String expirationDate;

        private boolean isActive;

        // TODO: child -> childId, cardId -> id 통일화 여부 물어보기
        public static MyCardResDto toDto(int childId, Card card, Balance balance) {
                return MyCardResDto.builder()
                        .childId(childId)
                        .cardId(card.getId())
                        .balance(balance.getCurrentBalance())
                        .cardToken(card.getCardToken())
                        .expirationDate(card.getExpirationDate().toString())
                        .isActive(card.isActive())
                        .build();
        }
}
