package com.stillalive.Ssook_BE.user.dto;

import com.stillalive.Ssook_BE.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ChildSignupRequestDto {

    private String name;
    private String tel;
    private Date bday;
    private Gender gender;
    private String loginId;
    private String password;
    private Integer schoolId;

}
