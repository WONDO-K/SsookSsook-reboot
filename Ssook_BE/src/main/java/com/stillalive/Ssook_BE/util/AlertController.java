package com.stillalive.Ssook_BE.util;


import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import com.stillalive.Ssook_BE.user.service.ChildService;
import com.stillalive.Ssook_BE.user.service.ParentService;
import com.stillalive.Ssook_BE.util.alert.AlertService;
import com.stillalive.Ssook_BE.util.alert.dto.AlertDto;
import com.stillalive.Ssook_BE.util.alert.dto.ConnectAlertReqDto;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "알림 구독", description = "사용자의 실시간 알림을 구독하는 API")
    public Flux<AlertDto> getUserAlerts(@RequestParam int userId, @RequestParam boolean isParent) {
        return alertService.getAlertsForUser(userId, isParent);
    }

    @GetMapping("/list")
    @Operation(summary = "알림 히스토리 조회", description = "사용자의 알림 히스토리를 조회하는 API")
    public ResponseEntity<ApiResponse<?>> getAlertHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId = userDetails.isParent() ? userDetails.getParentId() : userDetails.getChildId();
        List<AlertDto> alertHistory = alertService.getAlertHistory(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "알림 히스토리 조회 성공", alertHistory));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "읽지 않은 알림 수 조회", description = "사용자의 읽지 않은 알림 수를 조회하는 API")
    public ResponseEntity<ApiResponse<?>> getUnreadAlertsCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId = userDetails.isParent() ? userDetails.getParentId() : userDetails.getChildId();
        long unreadCount = alertService.countUnreadAlerts(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),"읽지 않은 알림 수 조회 성공", unreadCount));
    }

    @PutMapping("/read/{alertId}")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리하는 API")
    public ResponseEntity<ApiResponse<?>> markAsRead(@PathVariable int alertId) {
        alertService.markAsRead(alertId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "알림이 읽음 처리되었습니다.", null));
    }

    @PutMapping("/read-all")
    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 처리하는 API")
    public ResponseEntity<ApiResponse<?>> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId = userDetails.isParent() ? userDetails.getParentId() : userDetails.getChildId();
        alertService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),"모든 알림이 읽음 처리되었습니다.", null));
    }
}