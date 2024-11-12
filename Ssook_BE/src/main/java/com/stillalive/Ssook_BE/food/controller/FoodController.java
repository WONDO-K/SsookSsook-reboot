package com.stillalive.Ssook_BE.food.controller;


import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.food.service.FoodService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Food Controller", description = "음식 API")
@RequestMapping("/api/v1/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @Operation(summary = "음식 메뉴 추천", description = "자녀의 음식을 추천해드립니다.")
    @GetMapping("/reco")
    public ResponseEntity<ApiResponse<List<String>>> getRecoFood(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Integer childId = customUserDetails.getChildId();

        List<String> response = foodService.getRecoFood(childId);
        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "아이의 메뉴 추천 완료",
                        response
                ));
    }
}
