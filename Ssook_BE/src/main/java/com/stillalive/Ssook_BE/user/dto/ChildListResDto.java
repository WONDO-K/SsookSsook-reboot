package com.stillalive.Ssook_BE.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChildListResDto {
    private List<ChildResDto> childList;
    private Integer totalItems;
}