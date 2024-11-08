package com.stillalive.Ssook_BE.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyChildPointResDto {
        private int childId; // 자녀 ID
        private String childName; // 자녀 이름
        private int point; // 포인트
}
