package com.stillalive.Ssook_BE.util.alert;

import com.stillalive.Ssook_BE.util.alert.dto.AlertDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface AlertService {
    SseEmitter getAlertsForUser(int userId, boolean isParent); // 실시간 알림 제공

    void sendAlert(int userId, AlertDto alertDto); // 특정 사용자에게 알림 전송

    List<AlertDto> getAlertHistory(int userId); // 사용자의 알림 히스토리 조회

    long countUnreadAlerts(int userId); // 읽지 않은 알림 수 조회

    void markAsRead(int alertId); // 특정 알림 읽음 처리

    void markAllAsRead(int userId); // 모든 알림 읽음 처리

}
