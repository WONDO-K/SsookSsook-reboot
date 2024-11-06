package com.stillalive.Ssook_BE.nut.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DayIntakeNutResDto {

    private final Integer childId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate eatDate;

    // 영양소
    private final Float cal;
    private final Float carb;
    private final Float protein;
    private final Float fat;
    private final Float vitA;
    private final Float vitC;
    private final Float ribof;
    private final Float thiam;
    private final Float iron;
    private final Float calcium;

}
