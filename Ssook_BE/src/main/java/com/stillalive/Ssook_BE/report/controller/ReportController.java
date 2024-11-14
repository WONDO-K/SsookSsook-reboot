package com.stillalive.Ssook_BE.report.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.report.dto.ReportListResDto;
import com.stillalive.Ssook_BE.report.dto.ReportResDto;
import com.stillalive.Ssook_BE.report.service.ReportService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.service.ChildService;
import com.stillalive.Ssook_BE.user.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Report Controller", description = "리포트 API")
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "주간 리포트 생성 및 리스트 조회", description = "주간 리포트 리스트를 생성 및 조회합니다.")
    @GetMapping("/list/{child_id}")
    public ResponseEntity<ApiResponse<ReportListResDto>> getReportList(@PathVariable("child_id") Integer child_id,
                                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ReportListResDto response=reportService.generateReportAndGetList(child_id);
        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "주간 리포트 목록을 조회합니다.",
                        response
                )
        );
        //  ResponseEntity<ApiResponse<>>
    }
//  ResponseEntity<ApiResponse<>>

    @Operation(summary = "주간 리포트 상세조회", description = "주간 리포트를 조회합니다.")
    @GetMapping("/{report_id}")
    public ResponseEntity<ApiResponse<ReportResDto>> getReportDetail(@PathVariable("report_id") Integer report_id ) {

            ReportResDto response=reportService.getReportDetail(report_id);
            return ResponseEntity.ok(
                    ApiResponse.of(
                            200,
                            "OK",
                            "주간 리포트를 조회합니다.",
                            response
                    )
            );
    }

}
