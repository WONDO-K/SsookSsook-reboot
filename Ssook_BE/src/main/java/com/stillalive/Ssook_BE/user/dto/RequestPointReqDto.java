package com.stillalive.Ssook_BE.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPointReqDto {

    private int parentId; // 부모 ID

    @Builder.Default
    private Integer childId = null; // 자녀 ID

    private String message; // 요청 메시지

}
