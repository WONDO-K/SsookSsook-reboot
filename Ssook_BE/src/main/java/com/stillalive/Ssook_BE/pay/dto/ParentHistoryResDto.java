package com.stillalive.Ssook_BE.pay.dto;

import com.stillalive.Ssook_BE.domain.ParentHistory;
import com.stillalive.Ssook_BE.enums.PayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentHistoryResDto {
    
    private Integer id;
    private Integer parentId; // Parent의 ID (parent 객체의 ID)
    private PayType type;
    private Integer price; // 결제 금액
    private Integer balance; // 잔액
    private LocalDateTime createdAt;

    public static ParentHistoryResDto toDto(ParentHistory parentHistory) {
        return ParentHistoryResDto.builder()
                .id(parentHistory.getId())
                .parentId(parentHistory.getParent().getParentId())
                .type(parentHistory.getType())
                .price(parentHistory.getPrice())
                .balance(parentHistory.getBalance())
                .createdAt(parentHistory.getCreatedAt())
                .build();
    }

}
