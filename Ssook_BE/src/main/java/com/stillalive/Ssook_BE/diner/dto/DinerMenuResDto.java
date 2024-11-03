package com.stillalive.Ssook_BE.diner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DinerMenuResDto {

    private Integer menuId;
    private String name;
    private Integer price;
}
