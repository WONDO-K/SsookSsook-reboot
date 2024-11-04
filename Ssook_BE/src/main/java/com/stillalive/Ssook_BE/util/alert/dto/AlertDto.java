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
    private Long id; // 알림의 고유 ID
    private Long senderId; // 알림을 보낸 사용자 ID (부모 또는 자녀)
    private String senderNickname; // 알림을 보낸 사용자 닉네임
    private Long receiverId; // 알림을 받을 사용자 ID (부모 또는 자녀)
    private Long relatedId; // 알림과 연관된 ID (예: 배틀 또는 다른 관련 항목 ID)
    private String title; // 알림 제목
    private String message; // 알림 메시지
    private LocalDateTime timestamp; // 알림 발생 시간
    private boolean isRead; // 알림 읽음 여부
}
