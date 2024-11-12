package com.stillalive.Ssook_BE.user.dto;

import com.stillalive.Ssook_BE.enums.Activity;
import com.stillalive.Ssook_BE.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ChildDetailResDto {
    private Integer childId;
    private String name;
    private String tel;
    private LocalDate bday;
    private Gender gender;
    private Integer point;
    private String schoolName;
    private Float height;
    private Float weight;
    private Activity activity;
}
