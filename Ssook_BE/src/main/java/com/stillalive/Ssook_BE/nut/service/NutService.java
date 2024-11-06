package com.stillalive.Ssook_BE.nut.service;

import com.stillalive.Ssook_BE.domain.NutHistory;
import com.stillalive.Ssook_BE.enums.Meal;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.nut.dto.IntakeNutResDto;
import com.stillalive.Ssook_BE.nut.repository.NutHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.authenticator.SingleSignOnSessionKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NutService {

    private final NutHistoryRepository nutHistoryRepository;
    
    // 자녀 영양 섭취
    @Transactional
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


}
