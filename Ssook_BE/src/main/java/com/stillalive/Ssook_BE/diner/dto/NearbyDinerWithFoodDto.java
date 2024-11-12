package com.stillalive.Ssook_BE.diner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDinerWithFoodDto
{
    private String menuName;
    private Double lat;
    private Double lng;
    private Float range;    // km 단위
}
