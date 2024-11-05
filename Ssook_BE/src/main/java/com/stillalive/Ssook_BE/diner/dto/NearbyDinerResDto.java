package com.stillalive.Ssook_BE.diner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDinerResDto
{
    private Float lat;
    private Float lng;
    private Float range;    // km 단위
}
