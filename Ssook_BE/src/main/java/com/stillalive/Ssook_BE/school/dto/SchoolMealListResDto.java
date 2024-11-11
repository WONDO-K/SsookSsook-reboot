package com.stillalive.Ssook_BE.school.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SchoolMealListResDto {
    private Integer schoolCode;
    private List<SchoolMealDetailDto> schoolMealList;
    private Integer totalItems;
}
