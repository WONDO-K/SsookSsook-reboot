package com.stillalive.Ssook_BE.school.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.Parent;
import com.stillalive.Ssook_BE.enums.Meal;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.school.dto.*;
import com.stillalive.Ssook_BE.school.service.SchoolService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.dto.ParentListResDto;
import com.stillalive.Ssook_BE.user.service.ChildService;
import com.stillalive.Ssook_BE.user.service.ParentService;
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
    private final ChildService childService;
    private final ParentService parentService;

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
        
        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "주간 급식 메뉴를 조회합니다.",
                        schoolMealListResDto
                )
        );
    }

    @Operation(summary = "(부모용) 자녀 주간 급식 메뉴 조회", description = "자녀 주간 급식 메뉴를 조회합니다.")
    @GetMapping("/{childId}/week")
    public ResponseEntity<ApiResponse<SchoolMealListResDto>> getChildDinerList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @PathVariable Integer childId,
                                                                          @RequestParam(value = "date", required = false) String dateStr) {
        // 자녀 식별
        Child child = childService.getChildById(childId);
        log.info("child: " + child);

        // 부모 식별
        Parent parent = parentService.getParentById(customUserDetails.getParentId());
        log.info("parent: " + parent);

        // 부모의 자녀인지 확인
        ParentListResDto parentListResDto = childService.getParentList(child.getChildId());
        log.info("parentListResDto: " + parentListResDto);

        if (parentListResDto.getParentList().stream().noneMatch(p -> p.getParentId().equals(parent.getParentId()))) {
            throw new SsookException(ErrorCode.NOT_PARENT_CHILD);
        }

        // 학교 식별
        Integer schoolCode = child.getSchool().getCode();

        // 날자 식별
        LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();    // 없으면 오늘 날짜
        LocalDate startOfweek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 주간 급식표 조회
        SchoolMealListResDto schoolMealListResDto = schoolService.getSchoolMealList(schoolCode, startOfweek, endOfWeek);

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

    @Operation(summary = "(부모용) 자녀 급식 메뉴 상세 조회", description = "자녀 급식 메뉴 상세를 조회합니다.")
    @GetMapping("/{childId}/{schoolMealId}")
    public ResponseEntity<ApiResponse<SchoolMealDetailDto>> getChildSchoolMealDetail(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                     @PathVariable Integer childId,
                                                                                     @PathVariable Integer schoolMealId) {

        // 자녀 식별
        Child child = childService.getChildById(childId);

        // 부모 식별
        Parent parent = parentService.getParentById(customUserDetails.getParentId());

        // 부모의 자녀인지 확인
        ParentListResDto parentListResDto = childService.getParentList(child.getChildId());
        if (parentListResDto.getParentList().stream().noneMatch(p -> p.getParentId().equals(parent.getParentId()))) {
            throw new SsookException(ErrorCode.NOT_PARENT_CHILD);
        }

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

    @Operation(summary = "자녀가 해당일 급식을 먹음. 영양소 증가", description = "자녀가 해당일 급식을 먹음. 영양소 증가")
    @PostMapping("/meal")
    public ResponseEntity<ApiResponse<Void>> eatSchoolMeal(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                                      @RequestParam(required = false) Meal meal
    ) {

        if (customUserDetails.isParent()) {
            throw new SsookException(ErrorCode.NOT_CHILD_BUT_PARENT);
        }

        Integer childId = customUserDetails.getChildId();
        
        date = (date != null) ? date : new Date();    // 없으면 오늘 날짜
        meal = (meal != null) ? meal : Meal.LUNCH;    // 없으면 점심


        schoolService.eatSchoolMeal(childId, date, meal);

        log.info(childId + " 자녀가 " + date + "에 " + meal + "을 먹음. 영양소 증가");

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "자녀가 해당일 " + meal + "을 먹음. 영양소 증가",
                        null
                )
        );
    }

}