package com.stillalive.Ssook_BE.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FamilyReqResDto {

    private Integer familyRelationId;
    private String parentName;
    private String parentTel;
    private LocalDateTime requestedAt;
}
