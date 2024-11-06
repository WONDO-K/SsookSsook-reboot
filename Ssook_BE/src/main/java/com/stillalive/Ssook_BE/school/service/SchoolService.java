package com.stillalive.Ssook_BE.school.service;

import com.stillalive.Ssook_BE.domain.SchoolMeal;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.school.dto.SchoolListResDto;
import com.stillalive.Ssook_BE.school.dto.SchoolMealDetailDto;
import com.stillalive.Ssook_BE.school.dto.SchoolMealListResDto;
import com.stillalive.Ssook_BE.school.repository.SchoolMealRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_SCHOOL_MEAL));

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

    public List<SchoolListResDto> searchSchool(String name) {
        // 정확히 일치하는 학교가 있을 경우 가져오기
        Optional<Tuple> exactMatch = schoolMealRepository.findSchoolByExactMatch(name);

        // 검색어로 시작하는 학교 목록 가져오기
        List<Tuple> startingWithMatch = schoolMealRepository.findSchoolByStartingWith(name);

        // 검색어가 포함된 학교 목록 가져오기
        List<Tuple> containingMatch = schoolMealRepository.findSchoolByContaining(name);

        // 결과 리스트 생성
        List<SchoolListResDto> result = new ArrayList<>();

        // 정확히 일치하는 항목을 맨 앞에 추가
        exactMatch.ifPresent(school -> result.add(new SchoolListResDto(
                school.get("code", Integer.class),
                school.get("name", String.class)
        )));

        // 검색어로 시작하는 항목 추가 (이미 추가된 정확 일치 항목은 제외)
        result.addAll(startingWithMatch.stream()
                .filter(school -> result.stream().noneMatch(existing ->
                        existing.getName().equals(school.get("name", String.class))))
                .map(school -> new SchoolListResDto(
                        school.get("code", Integer.class),
                        school.get("name", String.class)
                ))
                .collect(Collectors.toList()));

        // 검색어가 포함된 항목 추가 (이미 추가된 항목은 제외)
        result.addAll(containingMatch.stream()
                .filter(school -> result.stream().noneMatch(existing ->
                        existing.getName().equals(school.get("name", String.class))))
                .map(school -> new SchoolListResDto(
                        school.get("code", Integer.class),
                        school.get("name", String.class)
                ))
                .collect(Collectors.toList()));

        // 최대 5개의 결과만 반환
        return result.stream().limit(10).collect(Collectors.toList());
    }
}