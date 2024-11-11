package com.stillalive.Ssook_BE.user.dto;

import com.stillalive.Ssook_BE.enums.Activity;
import lombok.Getter;

@Getter
public class BodyProfileReqDto {

    private Float height;
    private Float weight;

    private Activity activity;
}
