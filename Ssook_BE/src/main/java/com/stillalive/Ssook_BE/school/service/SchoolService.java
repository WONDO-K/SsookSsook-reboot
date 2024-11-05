package com.stillalive.Ssook_BE.school.service;

import com.stillalive.Ssook_BE.domain.SchoolMeal;
import com.stillalive.Ssook_BE.school.dto.SchoolMealDetailDto;
import com.stillalive.Ssook_BE.school.dto.SchoolMealListResDto;
import com.stillalive.Ssook_BE.school.repository.SchoolMealRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolMealRepository schoolMealRepository;

    // 주간 급식 리스트 조회
    public SchoolMealListResDto getSchoolMealList(Integer schoolCode, LocalDate startOfWeek, LocalDate endOfWeek) {

        // 이번 주 급식 조회
        List<SchoolMeal> weeklyMeals = schoolMealRepository.findBySchoolCodeAndDateBetween(schoolCode, startOfWeek, endOfWeek);

        // SchoolMeal 엔티티 리스트를 SchoolMealListResDto로 변환
        List<SchoolMealDetailDto> schoolMealList = weeklyMeals.stream()
                .map(schoolMeal -> SchoolMealDetailDto.builder()
                        .id(schoolMeal.getSchoolMealId())
                        .date(schoolMeal.getDate())
                        .menu(schoolMeal.getMenu())
                        .meal(schoolMeal.getMeal())
                        .nutrient(null)
                        .build())
                .toList();

        // DTO로 변환 후 반환
        return SchoolMealListResDto.builder()
                .schoolMealList(schoolMealList)
                .totalItems(schoolMealList.size())
                .build();
    }

    public SchoolMealDetailDto getSchoolMealDetail(Integer schoolMealId) {
        SchoolMeal schoolMeal = schoolMealRepository.findById(schoolMealId)
                .orElseThrow(() -> new EntityNotFoundException("School meal not found"));

        // 영양소 데이터를 Map에 추가
        Map<String, Float> nutrients = new HashMap<>();
        nutrients.put("cal", schoolMeal.getNutrient().getCal());
        nutrients.put("carb", schoolMeal.getNutrient().getCarb());
        nutrients.put("protein", schoolMeal.getNutrient().getProtein());
        nutrients.put("fat", schoolMeal.getNutrient().getFat());
        nutrients.put("vitA", schoolMeal.getNutrient().getVitA());
        nutrients.put("vitC", schoolMeal.getNutrient().getVitC());
        nutrients.put("ribof", schoolMeal.getNutrient().getRibof());
        nutrients.put("thiam", schoolMeal.getNutrient().getThiam());
        nutrients.put("iron", schoolMeal.getNutrient().getIron());
        nutrients.put("calcium", schoolMeal.getNutrient().getCalcium());


        // DTO로 변환 후 반환
        return SchoolMealDetailDto.builder()
                .id(schoolMeal.getSchoolMealId())
                .date(schoolMeal.getDate())
                .menu(schoolMeal.getMenu())
                .meal(schoolMeal.getMeal())
                .nutrient(nutrients)
                .build();
    }

}