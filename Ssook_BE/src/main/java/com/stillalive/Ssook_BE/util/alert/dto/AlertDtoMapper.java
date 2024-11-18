package com.stillalive.Ssook_BE.util.alert.dto;

import com.stillalive.Ssook_BE.user.service.ChildService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class AlertDtoMapper {

    private static final Logger log = LoggerFactory.getLogger(AlertDtoMapper.class);


    public static AlertDto toRequestPointAlert(int receiverId, String childName, String message) {
        AlertDto alertDto = AlertDto.builder()
                .receiverId(receiverId)
                .title(childName + "님의 포인트 요청")
                .message(message)
                .timestamp(LocalDateTime.now())
                .senderIsParent(false)
                .build();

        // 생성된 DTO에 대한 로그
        log.info("AlertDto 생성 확인 - senderIsParent: {}", alertDto.isSenderIsParent());
        return alertDto;
    }

    public static AlertDto toTransferPointAlert(int receiverId, int transferredAmount) {
        AlertDto alertDto = AlertDto.builder()
                .receiverId(receiverId)
                .title("포인트 전송")
                .message("부모님이 " + transferredAmount + "포인트를 전송했습니다.")
                .timestamp(LocalDateTime.now())
                .senderIsParent(true)
                .build();

        // 생성된 DTO에 대한 로그
        log.info("AlertDto 생성 확인 - senderIsParent: {}", alertDto.isSenderIsParent());
        return alertDto;
    }

    // 자녀 추가 알림 DTO 생성
    public static AlertDto toAddChildAlert(int receiverId, String parentName ,String childName) {
        AlertDto alertDto = AlertDto.builder()
                .receiverId(receiverId)
                .title("자녀 추가")
                .message(parentName + "님이 " + childName + "님을 자녀로 추가했습니다.")
                .timestamp(LocalDateTime.now())
                .senderIsParent(false)
                .build();

        // 생성된 DTO에 대한 로그
        log.info("AlertDto 생성 확인 - senderIsParent: {}", alertDto.isSenderIsParent());
        return alertDto;
    }

    // 자녀 수락 알림 DTO 생성
    public static AlertDto toAcceptChildAlert(int receiverId, String childName) {
        AlertDto alertDto = AlertDto.builder()
                .receiverId(receiverId)
                .title("자녀 수락")
                .message(childName + "님이 자녀 요청을 수락했습니다.")
                .timestamp(LocalDateTime.now())
                .senderIsParent(true)
                .build();

        // 생성된 DTO에 대한 로그
        log.info("AlertDto 생성 확인 - senderIsParent: {}", alertDto.isSenderIsParent());
        return alertDto;
    }


}