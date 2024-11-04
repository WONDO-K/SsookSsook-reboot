package com.stillalive.Ssook_BE.diner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DinerResDto {
    private Integer dinerId;
    private String name;
    private String address;
    private float lat;
    private float lng;
    private String tel;
    private Boolean isAngel;
}
