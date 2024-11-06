package com.stillalive.Ssook_BE.util.alert.dto;

import java.time.LocalDateTime;

public class AlertDtoMapper {

    public static AlertDto toRequestPointAlert(int receiverId, String childName) {
        AlertDto alertDto = AlertDto.builder()
                .receiverId(receiverId)
                .title("포인트 요청")
                .message(childName + "님이 포인트를 요청했습니다.")
                .timestamp(LocalDateTime.now())
                .senderIsParent(false)
                .build();

        // 생성된 DTO에 대한 로그
        System.out.println("AlertDto 생성 확인 - senderIsParent: " + alertDto.isSenderIsParent());

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
        System.out.println("AlertDto 생성 확인 - senderIsParent: " + alertDto.isSenderIsParent());

        return alertDto;
    }

}