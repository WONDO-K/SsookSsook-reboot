package com.stillalive.Ssook_BE.diner.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.diner.dto.*;
import com.stillalive.Ssook_BE.diner.service.DinerService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "주변 식당 목록 조회", description = "주변 식당 목록을 조회합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<DinerListResDto>> getNearbyDiners(@AuthenticationPrincipal CustomUserDetails customUserDetails
            , @RequestParam Float lat, @RequestParam Float lng, @RequestParam Float range) {

        NearbyDinerResDto nearbyDinerResDto = new NearbyDinerResDto(lat, lng, range);

        DinerListResDto dinerListResDto = dinerService.getNearbyDiners(nearbyDinerResDto);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "주변 식당 목록을 조회합니다.",
                        dinerListResDto
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

    @Operation(summary = "식당 메뉴 목록 조회", description = "식당 메뉴 목록을 조회합니다.")
    @GetMapping("/{dinerId}/menu")
    public ResponseEntity<ApiResponse<DinerMenuListResDto>> getDinerMenu(@AuthenticationPrincipal CustomUserDetails customUserDetails
            , @PathVariable Integer dinerId) {

        DinerMenuListResDto dinerMenuListResDto = dinerService.getDinerMenuList(dinerId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "식당 메뉴 목록을 조회합니다.",
                        dinerMenuListResDto
                )
        );
    }

    @Operation(summary = "[미완성] 추천메뉴 판매식당 목록 조회", description = "추천 메뉴를 판매하는 식당 목록을 조회합니다.")
    @Deprecated
    @GetMapping("/{foodId}")
    public ResponseEntity<ApiResponse<DinerListResDto>> getDinerListByFood(@AuthenticationPrincipal CustomUserDetails customUserDetails
            , @PathVariable Integer foodId) {

        DinerListResDto dinerListResDto = null;

//        dinerListResDto = dinerService.getDinerListByFood(foodId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "추천 메뉴를 판매하는 식당 목록을 조회합니다.",
                        dinerListResDto
                )
        );
    }




}
