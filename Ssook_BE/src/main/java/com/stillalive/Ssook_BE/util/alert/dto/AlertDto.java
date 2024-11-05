package com.stillalive.Ssook_BE.util.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 실시간 알림 전송용 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {
    private int id; // 알림의 고유 ID
    private int receiverId; // 알림을 받을 사용자 ID (부모 또는 자녀)
    private String title; // 알림 제목
    private String message; // 알림 메시지
    private LocalDateTime timestamp; // 알림 발생 시간
    private boolean isRead; // 알림 읽음 여부
}
