package com.stillalive.Ssook_BE.util.alert.dto;

import com.stillalive.Ssook_BE.domain.Alert;
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
    private boolean senderIsParent; // 보낸 사람이 부모인지 여부

    /**
     * Alert 엔티티를 AlertDto로 변환하는 정적 메서드
     */
    public static AlertDto toDto(Alert alert) {
        return AlertDto.builder()
                .id(alert.getId())
                .receiverId(alert.getReceiverId())
                .title(alert.getTitle())
                .message(alert.getMessage())
                .timestamp(alert.getTimestamp())
                .isRead(alert.isRead())
                .senderIsParent(alert.isSenderIsParent())
                .build();
    }
}