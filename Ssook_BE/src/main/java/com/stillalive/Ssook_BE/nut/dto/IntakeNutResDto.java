package com.stillalive.Ssook_BE.nut.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
public class IntakeNutResDto {

    private final Integer nutHistoryId;
    private final Integer childId;
    private final Date eatDate;
    private final String mealTime;

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
