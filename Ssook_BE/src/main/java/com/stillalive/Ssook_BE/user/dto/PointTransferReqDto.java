package com.stillalive.Ssook_BE.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointTransferReqDto {

        @Builder.Default
        private Integer parentId = null; // 부모 ID

        private int childId; // 자녀 ID

        private int amount; // 전송할 포인트

}
