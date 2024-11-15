package com.stillalive.Ssook_BE.nut.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.domain.FamilyRelation;
import com.stillalive.Ssook_BE.enums.Meal;
import com.stillalive.Ssook_BE.enums.Progress;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.nut.dto.*;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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

    @Operation(summary = "자녀 본인 하루 누적 영양섭취 조회", description = "자녀 본인 하루 누적 영양섭취 조회")
    @GetMapping("")
    public ResponseEntity<ApiResponse<DayIntakeNutResDto>> getDailyIntakeNut(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(value = "date", required = false) String dateStr) {

        Integer childId = customUserDetails.getChildId();

        LocalDate localDate = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();

        DayIntakeNutResDto dayIntakeNutResDto = nutService.getDailyIntakeNut(childId, localDate);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "자녀 본인의 하루 누적 영양섭취 조회가 완료되었습니다.",
                        dayIntakeNutResDto
                )
        );
    }

    @Operation(summary = "부모의 자녀 하루 누적 영양섭취 조회", description = "부모의 자녀 하루 누적 영양섭취 조회")
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<DayIntakeNutResDto>> getDailyIntakeNutByParent(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Integer childId, @RequestParam(value = "date", required = false) String dateStr) {

        // 본인의 자녀인지 확인
        Integer parentId = customUserDetails.getParentId();

        FamilyRelation familyRelation = familyRelationRepository.findByParent_ParentIdAndChild_ChildId(parentId, childId)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_FAMILY_RELATION));

        if (familyRelation.getStatus() != Progress.YES) {
            throw new SsookException(ErrorCode.NOT_YET_ACCEPTED_FAMILY_RELATION);
        }

        LocalDate localDate = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        DayIntakeNutResDto dayIntakeNutResDto = nutService.getDailyIntakeNut(childId, localDate);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "부모의 자녀 하루 누적 영양섭취 조회가 완료되었습니다.",
                        dayIntakeNutResDto
                )
        );
    }

    @Operation(summary = "자녀 본인 주간 누적 영양섭취 조회", description = "자녀 본인 주간 누적 영양섭취 조회")
    @GetMapping("/week")
    public ResponseEntity<ApiResponse<WeekIntakeNutResDto>> getWeeklyIntakeNut(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(value = "date", required = false) String dateStr) {

        Integer childId = customUserDetails.getChildId();

        LocalDate localDate = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();

        WeekIntakeNutResDto weekIntakeNutResDto = nutService.getWeeklyIntakeNut(childId, localDate);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "자녀 본인의 주간 누적 영양섭취 조회가 완료되었습니다.",
                        weekIntakeNutResDto
                )
        );
    }

    @Operation(summary = "부모의 자녀 주간 누적 영양섭취 조회", description = "부모의 자녀 주간 누적 영양섭취 조회")
    @GetMapping("/week/{childId}")
    public ResponseEntity<ApiResponse<WeekIntakeNutResDto>> getWeeklyIntakeNutByParent(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Integer childId, @RequestParam(value = "date", required = false) String dateStr) {

        // 본인의 자녀인지 확인
        Integer parentId = customUserDetails.getParentId();

        FamilyRelation familyRelation = familyRelationRepository.findByParent_ParentIdAndChild_ChildId(parentId, childId)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_FAMILY_RELATION));

        if (familyRelation.getStatus() != Progress.YES) {
            throw new SsookException(ErrorCode.NOT_YET_ACCEPTED_FAMILY_RELATION);
        }

        LocalDate localDate = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        WeekIntakeNutResDto weekIntakeNutResDto = nutService.getWeeklyIntakeNut(childId, localDate);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "부모의 자녀 주간 누적 영양섭취 조회가 완료되었습니다.",
                        weekIntakeNutResDto
                )
        );
    }

//    경로 변수:cild_id, 쿼리변수 : year, month 를 받아서 아이의 하루 영양 섭취량과 권장 섭취량을 조회한후 비교해서 결과를 반환하는 메소드
    @Operation(summary = "자녀 월간 하루 영양 섭취량 리스트 조회", description = "자녀 월간 하루 영양 섭취량 리스트 조회")
    @GetMapping("/score/{child_id}")
    public ResponseEntity<ApiResponse<NutScoreListResDto>> getMonthIntakeNut(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Integer child_id, @RequestParam(value = "year", required = false) Integer year, @RequestParam(value = "month", required = false) Integer month) {

        List<NutScoreResDto> result = nutService.getScoreList(child_id, year, month);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "자녀 월간 하루 영양 섭취량 리스트 조회가 완료되었습니다.",
                        NutScoreListResDto.builder()
                                .score_list(result)
                                .build()
                )
        );

    }



}
