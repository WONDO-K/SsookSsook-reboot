package com.stillalive.Ssook_BE.util.alert;

import com.stillalive.Ssook_BE.domain.Alert;
import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import com.stillalive.Ssook_BE.util.alert.dto.AlertDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertServiceImpl.class);

    private final Map<Integer, Sinks.Many<AlertDto>> userAlerts = new ConcurrentHashMap<>();
    private final AlertRepository alertRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    @Override
    public Flux<AlertDto> getAlertsForUser(int userId) {
        userAlerts.putIfAbsent(userId, Sinks.many().multicast().onBackpressureBuffer());
        log.info("User {}의 실시간 알림 스트림 요청됨.", userId);
        return userAlerts.get(userId).asFlux().delayElements(Duration.ofMillis(500));
    }

    @Override
    public void sendAlert(int userId, AlertDto alertDto) {
        Alert alert = Alert.builder()
                .senderId(userId)
                .receiverId(alertDto.getReceiverId())
                .title(alertDto.getTitle())
                .message(alertDto.getMessage())
                .timestamp(alertDto.getTimestamp())
                .isRead(false)
                .build();
        alertRepository.save(alert);
        log.info("데이터베이스에 알림 저장: {}", alert);

        userAlerts.computeIfPresent(userId, (id, sink) -> {
            sink.tryEmitNext(alertDto);
            return sink;
        });
        log.info("사용자 {}에게 알림 전송됨: {}", userId, alertDto);
    }

    @Override
    public List<AlertDto> getAlertHistory(int userId) {
        List<Alert> alerts;
        if (isParent(userId)) {
            alerts = alertRepository.findAllByReceiverIdOrderByTimestampDesc(userId);
        } else if (isChild(userId)) {
            alerts = alertRepository.findAllBySenderIdOrderByTimestampDesc(userId);
        } else {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다: " + userId);
        }

        return alerts.stream()
                .map(alert -> AlertDto.builder()
                        .id(alert.getId())
                        .receiverId(alert.getReceiverId())
                        .title(alert.getTitle())
                        .message(alert.getMessage())
                        .timestamp(alert.getTimestamp())
                        .isRead(alert.isRead())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public long countUnreadAlerts(int userId) {
        if (isParent(userId)) {
            return alertRepository.countByReceiverIdAndIsReadFalse(userId);
        } else if (isChild(userId)) {
            return alertRepository.countBySenderIdAndIsReadFalse(userId);
        } else {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다: " + userId);
        }
    }

    @Override
    public void markAsRead(int alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다: " + alertId));
        alert.setRead(true);
        alertRepository.save(alert);
    }

    @Override
    public void markAllAsRead(int userId) {
        List<Alert> alerts;
        if (isParent(userId)) {
            alerts = alertRepository.findAllByReceiverIdAndIsReadFalse(userId);
        } else if (isChild(userId)) {
            alerts = alertRepository.findAllBySenderIdAndIsReadFalse(userId);
        } else {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다: " + userId);
        }
        alerts.forEach(alert -> alert.setRead(true));
        alertRepository.saveAll(alerts);
    }

    private boolean isParent(int userId) {
        return parentRepository.existsById(userId);
    }

    private boolean isChild(int userId) {
        return childRepository.existsById(userId);
    }
}
