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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertServiceImpl.class);

    private final Map<Integer, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final AlertRepository alertRepository;
    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    @Override
    public SseEmitter getAlertsForUser(int userId, boolean isParent) {
        log.info("subscribeToRealtimeAlerts 호출됨 - userId: {}, isParent: {}", userId, isParent);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        userEmitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            log.info("SseEmitter 완료 - userId: {}", userId);
            userEmitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("SseEmitter 타임아웃 - userId: {}", userId);
            emitter.complete();
            userEmitters.remove(userId);
        });

        // 초기 알림을 가져와 클라이언트에 전송
        List<Alert> alerts = alertRepository.findByReceiverIdAndSenderIsParent(userId, !isParent);
        alerts.forEach(alert -> sendAlertToEmitter(emitter, AlertDto.toDto(alert)));

        return emitter;
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
                .senderIsParent(alertDto.isSenderIsParent())
                .build();

        // 데이터베이스에 저장하여 ID가 자동 생성되도록 함
        Alert savedAlert = alertRepository.save(alert);
        log.info("데이터베이스에 알림 저장: {}", savedAlert);
        log.info("데이터베이스에 저장된 Alert 엔티티 - senderIsParent 값: {}", savedAlert.isSenderIsParent());

        // 저장된 엔티티를 DTO로 변환
        AlertDto savedAlertDto = AlertDto.toDto(savedAlert);

        // SSE를 통해 실시간으로 알림 전송
        SseEmitter emitter = userEmitters.get(savedAlert.getReceiverId());
        if (emitter != null) {
            sendAlertToEmitter(emitter, savedAlertDto);
        }
    }

    private void sendAlertToEmitter(SseEmitter emitter, AlertDto alertDto) {
        try {
            emitter.send(SseEmitter.event().name("alerts").data(alertDto));
            log.info("실시간 알림 전송: {}", alertDto);
        } catch (IOException e) {
            log.error("SseEmitter 전송 실패", e);
            emitter.completeWithError(e);
        }
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
