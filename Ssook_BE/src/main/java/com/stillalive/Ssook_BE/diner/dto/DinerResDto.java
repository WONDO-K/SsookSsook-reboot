package com.stillalive.Ssook_BE.diner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DinerResDto {
    private Integer dinerId;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private String tel;
    private Boolean isAngel;
}
