package com.stillalive.Ssook_BE.util;


import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.util.alert.AlertService;
import com.stillalive.Ssook_BE.util.alert.dto.AlertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AlertDto> getUserAlerts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId = userDetails.isParent() ? userDetails.getParentId() : userDetails.getChildId();
        return alertService.getAlertsForUser(userId);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAlertHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId = userDetails.isParent() ? userDetails.getParentId() : userDetails.getChildId();
        List<AlertDto> alertHistory = alertService.getAlertHistory(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "알림 히스토리 조회 성공", alertHistory));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<?>> getUnreadAlertsCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId = userDetails.isParent() ? userDetails.getParentId() : userDetails.getChildId();
        long unreadCount = alertService.countUnreadAlerts(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),"읽지 않은 알림 수 조회 성공", unreadCount));
    }

    @PutMapping("/read/{alertId}")
    public ResponseEntity<ApiResponse<?>> markAsRead(@PathVariable int alertId) {
        alertService.markAsRead(alertId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "알림이 읽음 처리되었습니다.", null));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<?>> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId = userDetails.isParent() ? userDetails.getParentId() : userDetails.getChildId();
        alertService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),"모든 알림이 읽음 처리되었습니다.", null));
    }
}