package com.stillalive.Ssook_BE.util.alert;

import com.stillalive.Ssook_BE.domain.Alert;
import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import com.stillalive.Ssook_BE.util.alert.dto.AlertDto;
import com.stillalive.Ssook_BE.util.alert.dto.ConnectAlertReqDto;
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
    public Flux<AlertDto> getAlertsForUser(int userId, boolean isParent) {
        // 사용자별로 Sinks가 없으면 생성
        userAlerts.putIfAbsent(userId, Sinks.many().multicast().onBackpressureBuffer());

        // userId와 senderIsParent 조건에 맞는 알림 필터링
        // !isParent가 true이면(부모이면) senderIsParent는 false인 알림만 불러온다(자식이 보낸)
        // !isParent가 false이면(자식이면) senderIsParent는 true인 알림만 불러온다(부모가 보낸)
        List<Alert> alerts = alertRepository.findByReceiverIdAndSenderIsParent(userId, !isParent);

        // 필터링된 알림을 Flux로 변환하여 반환
        return Flux.fromIterable(alerts)
                .map(AlertDto::toDto) // 변환 메서드 사용
                .delayElements(Duration.ofMillis(500));
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
