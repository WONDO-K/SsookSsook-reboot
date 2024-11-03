package com.stillalive.Ssook_BE.diner.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.diner.dto.AngelListResDto;
import com.stillalive.Ssook_BE.diner.dto.DinerListResDto;
import com.stillalive.Ssook_BE.diner.dto.DinerResDto;
import com.stillalive.Ssook_BE.diner.service.DinerService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Diner Controller", description = "식당 API")
@RequestMapping("/api/v1/diner")
@RequiredArgsConstructor
public class DinerController {

    private final DinerService dinerService;

    @Operation(summary = "전체 식당 목록 확인", description = "전체 식당 목록을 확인합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<DinerListResDto>> getDinerList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        DinerListResDto dinerListResDto = dinerService.getDinerList();

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "선한영향력가게 목록을 확인합니다.",
                        dinerListResDto
                )
        );
    }

    @Operation(summary = "선한영향력가게 목록 확인", description = "선한영향력가게 목록을 확인합니다.")
    @GetMapping("/angel")
    public ResponseEntity<ApiResponse<AngelListResDto>> getAngelList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        AngelListResDto angelListResDto = dinerService.getAngelList();

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "선한영향력가게 목록을 확인합니다.",
                        angelListResDto
                )
        );
    }

    @Operation(summary = "식당 상세 조회", description = "식당 상세 정보를 조회합니다.")
    @GetMapping("/{dinerId}")
    public ResponseEntity<ApiResponse<DinerResDto>> getDinerDetail(@AuthenticationPrincipal CustomUserDetails customUserDetails
    , @PathVariable Integer dinerId) {

        DinerResDto dinerResDto = dinerService.getDinerDetail(dinerId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "식당 상세 정보를 조회합니다.",
                        dinerResDto
                )
        );
    }


}
