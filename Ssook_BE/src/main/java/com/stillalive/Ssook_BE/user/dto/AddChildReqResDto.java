package com.stillalive.Ssook_BE.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AddChildReqResDto {

    private Integer familyRelationId;
    private String childName;
    private String childTel;
    private LocalDateTime requestedAt;
}
