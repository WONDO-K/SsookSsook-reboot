package com.stillalive.Ssook_BE.nut.service;

import com.stillalive.Ssook_BE.domain.NutHistory;
import com.stillalive.Ssook_BE.enums.Meal;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.nut.dto.DayIntakeNutResDto;
import com.stillalive.Ssook_BE.nut.dto.IntakeNutResDto;
import com.stillalive.Ssook_BE.nut.dto.WeekIntakeNutResDto;
import com.stillalive.Ssook_BE.nut.repository.NutHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.authenticator.SingleSignOnSessionKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NutService {

    private final NutHistoryRepository nutHistoryRepository;
    
    // 자녀 영양 섭취
    @Transactional(readOnly = true)
    public IntakeNutResDto getIntakeNut(Integer childId, LocalDate date, Meal mealTime) {
        // 영양 섭취 조회
        NutHistory nutHistory = nutHistoryRepository.findByChild_ChildIdAndEatDateAndMeal(childId, date, mealTime)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_NUT_HISTORY));

        return IntakeNutResDto.builder()
                .nutHistoryId(nutHistory.getId())
                .childId(nutHistory.getChild().getChildId())
                .eatDate(nutHistory.getEatDate())
                .mealTime(nutHistory.getMeal().name())
                .cal(nutHistory.getNutrient().getCal())
                .carb(nutHistory.getNutrient().getCarb())
                .protein(nutHistory.getNutrient().getProtein())
                .fat(nutHistory.getNutrient().getFat())
                .vitA(nutHistory.getNutrient().getVitA())
                .vitC(nutHistory.getNutrient().getVitC())
                .ribof(nutHistory.getNutrient().getRibof())
                .thiam(nutHistory.getNutrient().getThiam())
                .iron(nutHistory.getNutrient().getIron())
                .calcium(nutHistory.getNutrient().getCalcium())
                .build();
    }

    // 자녀 하루 누적 섭취량 조회
    @Transactional(readOnly = true)
    public DayIntakeNutResDto getDailyIntakeNut(Integer childId, LocalDate date) {
        // 하루 누적 섭취량 조회
        List<NutHistory> nutHistories = nutHistoryRepository.findAllByChild_ChildIdAndEatDate(childId, date)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_NUT_HISTORY));

        // 하루 누적 섭취량 계산
        Float dailyCal = 0f;
        Float dailyCarb = 0f;
        Float dailyProtein = 0f;
        Float dailyFat = 0f;
        Float dailyVitA = 0f;
        Float dailyVitC = 0f;
        Float dailyRibof = 0f;
        Float dailyThiam = 0f;
        Float dailyIron = 0f;
        Float dailyCalcium = 0f;

        for (NutHistory nutHistory : nutHistories) {
            dailyCal += nutHistory.getNutrient().getCal() == null ? 0 : nutHistory.getNutrient().getCal();
            dailyCarb += nutHistory.getNutrient().getCarb() == null ? 0 : nutHistory.getNutrient().getCarb();
            dailyProtein += nutHistory.getNutrient().getProtein() == null ? 0 : nutHistory.getNutrient().getProtein();
            dailyFat += nutHistory.getNutrient().getFat() == null ? 0 : nutHistory.getNutrient().getFat();
            dailyVitA += nutHistory.getNutrient().getVitA() == null ? 0 : nutHistory.getNutrient().getVitA();
            dailyVitC += nutHistory.getNutrient().getVitC() == null ? 0 : nutHistory.getNutrient().getVitC();
            dailyRibof += nutHistory.getNutrient().getRibof() == null ? 0 : nutHistory.getNutrient().getRibof();
            dailyThiam += nutHistory.getNutrient().getThiam() == null ? 0 : nutHistory.getNutrient().getThiam();
            dailyIron += nutHistory.getNutrient().getIron() == null ? 0 : nutHistory.getNutrient().getIron();
            dailyCalcium += nutHistory.getNutrient().getCalcium() == null ? 0 : nutHistory.getNutrient().getCalcium();
        }

        return DayIntakeNutResDto.builder()
                .childId(childId)
                .eatDate(date)
                .cal(dailyCal)
                .carb(dailyCarb)
                .protein(dailyProtein)
                .fat(dailyFat)
                .vitA(dailyVitA)
                .vitC(dailyVitC)
                .ribof(dailyRibof)
                .thiam(dailyThiam)
                .iron(dailyIron)
                .calcium(dailyCalcium)
                .build();
    }

    // 자녀 주간 누적 섭취량 조회
    @Transactional(readOnly = true)
    public WeekIntakeNutResDto getWeeklyIntakeNut(Integer childId, LocalDate date) {
        // 주간 누적 섭취량 조회
        List<NutHistory> nutHistories = nutHistoryRepository.findAllByChild_ChildIdAndEatDateBetween(childId, date.minusDays(6), date)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_NUT_HISTORY));

        // 주간 누적 섭취량 계산
        Float weeklyCal = 0f;
        Float weeklyCarb = 0f;
        Float weeklyProtein = 0f;
        Float weeklyFat = 0f;
        Float weeklyVitA = 0f;
        Float weeklyVitC = 0f;
        Float weeklyRibof = 0f;
        Float weeklyThiam = 0f;
        Float weeklyIron = 0f;
        Float weeklyCalcium = 0f;

        for (NutHistory nutHistory : nutHistories) {
            weeklyCal += nutHistory.getNutrient().getCal() == null ? 0 : nutHistory.getNutrient().getCal();
            weeklyCarb += nutHistory.getNutrient().getCarb() == null ? 0 : nutHistory.getNutrient().getCarb();
            weeklyProtein += nutHistory.getNutrient().getProtein() == null ? 0 : nutHistory.getNutrient().getProtein();
            weeklyFat += nutHistory.getNutrient().getFat() == null ? 0 : nutHistory.getNutrient().getFat();
            weeklyVitA += nutHistory.getNutrient().getVitA() == null ? 0 : nutHistory.getNutrient().getVitA();
            weeklyVitC += nutHistory.getNutrient().getVitC() == null ? 0 : nutHistory.getNutrient().getVitC();
            weeklyRibof += nutHistory.getNutrient().getRibof() == null ? 0 : nutHistory.getNutrient().getRibof();
            weeklyThiam += nutHistory.getNutrient().getThiam() == null ? 0 : nutHistory.getNutrient().getThiam();
            weeklyIron += nutHistory.getNutrient().getIron() == null ? 0 : nutHistory.getNutrient().getIron();
            weeklyCalcium += nutHistory.getNutrient().getCalcium() == null ? 0 : nutHistory.getNutrient().getCalcium();
        }

        return WeekIntakeNutResDto.builder()
                .childId(childId)
                .year(date.getYear())
                .month(date.getMonthValue())
                .weekOfMonth(date.get(WeekFields.ISO.weekOfMonth()))
                .cal(weeklyCal)
                .carb(weeklyCarb)
                .protein(weeklyProtein)
                .fat(weeklyFat)
                .vitA(weeklyVitA)
                .vitC(weeklyVitC)
                .ribof(weeklyRibof)
                .thiam(weeklyThiam)
                .iron(weeklyIron)
                .calcium(weeklyCalcium)
                .build();
    }

}
