package com.stillalive.Ssook_BE.school.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.school.dto.*;
import com.stillalive.Ssook_BE.school.service.SchoolService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

@RestController
@Tag(name = "School Controller", description = "학교 API")
@RequestMapping("/api/v1/school")
@RequiredArgsConstructor
public class SchoolController {

    private static final Logger log = LoggerFactory.getLogger(SchoolController.class);
    private final SchoolService schoolService;

    @Operation(summary = "주간 급식 메뉴 조회", description = "주간 급식 메뉴를 조회합니다.")
    @GetMapping("/week")
    public ResponseEntity<ApiResponse<SchoolMealListResDto>> getDinerList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestParam(value = "date", required = false) String dateStr) {

        // 학교 식별
        Integer schoolCode = customUserDetails.getSchoolCode();

        // 날자 식별
        LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();    // 없으면 오늘 날짜
        LocalDate startOfweek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 주간 급식표 조회
        SchoolMealListResDto schoolMealListResDto = schoolService.getSchoolMealList(schoolCode, startOfweek, endOfWeek);
        
        // TODO (chabs) null 뜨는 것 같은데

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
    
    @Operation(summary = "학교 검색", description = "학교 리스트를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SchoolListResDto>>> searchSchool(@RequestParam(value = "name", required = false) String name) {

        List<SchoolListResDto> schoolListResDto = schoolService.searchSchool(name);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "학교 리스트를 검색합니다.",
                        schoolListResDto
                )
        );
    }

    @Operation(summary = "자녀가 해당일 점심을 먹음. 영양소 증가", description = "자녀가 해당일 점심을 먹음. 영양소 증가")
    @PostMapping("/lunch")
    public ResponseEntity<ApiResponse<Void>> eatLunch(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        Integer childId = customUserDetails.getChildId();
        
        // 안돼서 임시로 // TODO(chabs)  // api 명세서 쓰기
        childId = (childId != null) ? childId : 3;    // 없으면 1번 자녀

        date = (date != null) ? date : new Date();    // 없으면 오늘 날짜

        log.info(childId + " 자녀가 " + date + "에 점심을 먹음. 영양소 증가");

        schoolService.eatLunch(childId, date);


        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "자녀가 해당일 점심을 먹음. 영양소 증가",
                        null
                )
        );
    }

}