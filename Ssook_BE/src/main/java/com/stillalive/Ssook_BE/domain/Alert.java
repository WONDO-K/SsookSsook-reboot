package com.stillalive.Ssook_BE.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 알림 엔티티 클래스
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 알림 고유 ID

    @Column(nullable = false)
    private int senderId; // 알림을 보낸 사용자 ID (부모 혹은 자식)

    @Column(nullable = false)
    private int receiverId; // 알림을 받을 사용자 ID (부모 혹은 자식)

    @Column(nullable = false)
    private String title; // 알림 제목

    @Column(nullable = false)
    private String message; // 알림 메시지 내용

    @Column(nullable = false)
    private LocalDateTime timestamp; // 알림 생성 시간

    @Column(nullable = false)
    private boolean isRead; // 알림 읽음 상태
}