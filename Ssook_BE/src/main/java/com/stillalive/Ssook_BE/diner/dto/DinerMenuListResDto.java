package com.stillalive.Ssook_BE.diner.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DinerMenuListResDto {

    private List<DinerMenuResDto> dinerMenuList;
    private Integer totalItems;
}
