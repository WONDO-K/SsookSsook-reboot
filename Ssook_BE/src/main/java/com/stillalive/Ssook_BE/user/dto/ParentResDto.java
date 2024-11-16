package com.stillalive.Ssook_BE.user.dto;

import com.stillalive.Ssook_BE.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
public class ParentResDto {
    private Integer parentId;
    private String name;
    private String tel;
    private LocalDate bday;
    private Gender gender;

    @Override
    public String toString() {
        return "ParentResDto{" +
                "parentId=" + parentId +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", bday=" + bday + '\'' +
                ", gender=" + gender +
                '}';
    }
}
