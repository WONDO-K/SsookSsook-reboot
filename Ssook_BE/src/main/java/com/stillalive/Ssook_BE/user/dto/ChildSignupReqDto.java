package com.stillalive.Ssook_BE.user.dto;

import com.stillalive.Ssook_BE.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildSignupReqDto {

    private String name;
    private String tel;
    private LocalDate bday;
    private Gender gender;
    private String loginId;
    private String password;
    private Integer schoolId;

}
