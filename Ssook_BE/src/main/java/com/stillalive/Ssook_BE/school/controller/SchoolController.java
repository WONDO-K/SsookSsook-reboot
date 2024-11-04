package com.stillalive.Ssook_BE.school.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.school.dto.*;
import com.stillalive.Ssook_BE.school.service.SchoolService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "School Controller", description = "학교 API")
@RequestMapping("/api/v1/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @Operation(summary = "[TO DO] 주간 급식 메뉴 조회", description = "주간 급식 메뉴를 조회합니다.")
    @Deprecated
    @GetMapping("/week")
    public ResponseEntity<ApiResponse<SchoolMealListResDto>> getDinerList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        SchoolMealListResDto schoolMealListResDto = schoolService.getSchoolMealList();

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "주간 급식 메뉴를 조회합니다.",
                        schoolMealListResDto
                )
        );
    }

    @Operation(summary = "급식 메뉴 상세 조회", description = "급식 메뉴 상세를 조회합니다.")
    @GetMapping("/{schoolMealId}")
    public ResponseEntity<ApiResponse<SchoolMealDetailDto>> getSchoolMealDetail(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Integer schoolMealId) {

        SchoolMealDetailDto schoolMealDetailDto = schoolService.getSchoolMealDetail(schoolMealId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "급식 메뉴 상세를 조회합니다.",
                        schoolMealDetailDto
                )
        );
    }

}