package com.stillalive.Ssook_BE.nut.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.enums.Meal;
import com.stillalive.Ssook_BE.nut.dto.IntakeNutResDto;
import com.stillalive.Ssook_BE.nut.service.NutService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Tag(name = "Nut Controller", description = "영양소 API")
@RequestMapping("/api/v1/nut")
@RequiredArgsConstructor
public class NutController {

    private final NutService nutService;

    @Operation(summary = "자녀 본인 끼니별 영양섭취 조회", description = "자녀 본인 끼니별 영양섭취 조회")
    @GetMapping("/meal")
    public ResponseEntity<ApiResponse<IntakeNutResDto>> getIntakeNut(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(value = "date", required = false) String dateStr, @RequestParam Meal mealTime) {

        Integer childId = customUserDetails.getChildId();

        LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();    // 없으면 오늘 날짜

        IntakeNutResDto intakeNutResDto = nutService.getIntakeNut(childId, date, mealTime);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "자녀 본인의 끼니별 영양섭취 조회가 완료되었습니다.",
                        intakeNutResDto
                )
        );
    }


}
