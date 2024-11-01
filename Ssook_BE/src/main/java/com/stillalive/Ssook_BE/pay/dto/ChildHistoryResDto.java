package com.stillalive.Ssook_BE.pay.dto;

import com.stillalive.Ssook_BE.domain.ChildHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildHistoryResDto {

    private Integer id;
    private LocalDateTime createdAt;
    private Integer cardId;
    private Integer cardPrice;
    private Integer pointPrice;
    private String historyType;
    private List<PayDetailDto> payDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PayDetailDto {
        private String menuName;
        private Integer quantity;
        private Integer price;
    }

    public static ChildHistoryResDto toDto(ChildHistory childHistory) {
        List<PayDetailDto> payDetails = childHistory.getPayDetails().stream().map(payDetail ->
                PayDetailDto.builder()
                        .menuName(payDetail.getMenu().getName())
                        .quantity(payDetail.getQuantity())
                        .price(payDetail.getMenu().getPrice())
                        .build()
        ).collect(Collectors.toList());

        return ChildHistoryResDto.builder()
                .id(childHistory.getId())
                .createdAt(childHistory.getHistoryTime())
                .cardId(childHistory.getCard().getId())
                .cardPrice(childHistory.getCardPrice())
                .pointPrice(childHistory.getPointPrice())
                .historyType(childHistory.getHistoryType().name())
                .payDetails(payDetails)
                .build();
    }
}
