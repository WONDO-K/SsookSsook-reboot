package com.stillalive.Ssook_BE.diner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuDinerResDto {

    private Integer dinerId;
    private String dinerName;
    private String category;
}
