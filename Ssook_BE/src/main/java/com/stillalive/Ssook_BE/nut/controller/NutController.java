package com.stillalive.Ssook_BE.nut.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.domain.FamilyRelation;
import com.stillalive.Ssook_BE.enums.Meal;
import com.stillalive.Ssook_BE.enums.Progress;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.nut.dto.IntakeNutResDto;
import com.stillalive.Ssook_BE.nut.service.NutService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.repository.FamilyRelationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Tag(name = "Nut Controller", description = "영양소 API")
@RequestMapping("/api/v1/nut")
@RequiredArgsConstructor
public class NutController {

    private final NutService nutService;
    private final FamilyRelationRepository familyRelationRepository;

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

    @Operation(summary = "부모의 자녀 영양섭취 조회", description = "부모의 자녀 영양섭취 조회")
    @GetMapping("/meal/{childId}")
    public ResponseEntity<ApiResponse<IntakeNutResDto>> getIntakeNutByParent(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Integer childId, @RequestParam(value = "date", required = false) String dateStr, @RequestParam Meal mealTime) {

        // 본인의 자녀인지 확인
        Integer parentId = customUserDetails.getParentId();

        FamilyRelation familyRelation = familyRelationRepository.findByParent_ParentIdAndChild_ChildId(parentId, childId)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_FAMILY_RELATION));

        if (familyRelation.getStatus() != Progress.YES) {
            throw new SsookException(ErrorCode.NOT_YET_ACCEPTED_FAMILY_RELATION);
        }

        LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();    // 없으면 오늘 날짜

        IntakeNutResDto intakeNutResDto = nutService.getIntakeNut(childId, date, mealTime);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "부모의 자녀 영양섭취 조회가 완료되었습니다.",
                        intakeNutResDto
                )
        );
    }



}
