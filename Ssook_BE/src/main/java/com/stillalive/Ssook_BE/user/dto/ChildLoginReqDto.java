package com.stillalive.Ssook_BE.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChildLoginReqDto {

    private String loginId;
    private String password;

}
