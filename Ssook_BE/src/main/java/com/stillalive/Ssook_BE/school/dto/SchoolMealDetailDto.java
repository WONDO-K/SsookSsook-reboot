package com.stillalive.Ssook_BE.school.dto;

import com.stillalive.Ssook_BE.enums.Meal;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class SchoolMealDetailDto {

    private Date date;
    private String menu;
    private Meal meal;
    private Map<String, Float> nutrient;

}
