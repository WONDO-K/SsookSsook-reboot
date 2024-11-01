package com.stillalive.Ssook_BE.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FamilyReqListResDto {
    private List<FamilyReqResDto> familyReqList;
}
