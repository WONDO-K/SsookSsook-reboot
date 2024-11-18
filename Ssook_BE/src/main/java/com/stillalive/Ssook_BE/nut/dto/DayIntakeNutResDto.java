package com.stillalive.Ssook_BE.nut.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
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

    // 영양소 추가하는 메서드
    public DayIntakeNutResDto addNutrient(Float cal, Float carb, Float protein, Float fat, Float vitA, Float vitC, Float ribof, Float thiam, Float iron, Float calcium) {
        return new DayIntakeNutResDto(childId, eatDate, this.cal + cal, this.carb + carb, this.protein + protein, this.fat + fat, this.vitA + vitA, this.vitC + vitC, this.ribof + ribof, this.thiam + thiam, this.iron + iron, this.calcium + calcium);
    }

}
