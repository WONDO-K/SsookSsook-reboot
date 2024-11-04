package com.stillalive.Ssook_BE.school.service;

import com.stillalive.Ssook_BE.domain.SchoolMeal;
import com.stillalive.Ssook_BE.school.dto.SchoolMealDetailDto;
import com.stillalive.Ssook_BE.school.dto.SchoolMealListResDto;
import com.stillalive.Ssook_BE.school.repository.SchoolMealRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolMealRepository schoolMealRepository;

    public SchoolMealListResDto getSchoolMealList() {
        // to do
        return null;
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
                .date(schoolMeal.getDate())
                .menu(schoolMeal.getMenu())
                .meal(schoolMeal.getMeal())
                .nutrient(nutrients)
                .build();
    }

}