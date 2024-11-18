package com.stillalive.Ssook_BE.user.dto;

import com.stillalive.Ssook_BE.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
public class ChildResDto {
    private Integer childId;
    private String name;
    private String tel;
    private LocalDate bday;
    private Gender gender;
    private Integer point;
    private String schoolName;
}
