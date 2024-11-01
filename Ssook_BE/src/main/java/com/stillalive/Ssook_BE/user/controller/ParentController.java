package com.stillalive.Ssook_BE.user.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.dto.ParentLoginReqDto;
import com.stillalive.Ssook_BE.user.dto.ParentSignupReqDto;
import com.stillalive.Ssook_BE.user.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Parent Controller", description = "부모 API")
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @Operation(summary = "부모 회원가입", description = "부모 회원가입을 진행합니다.")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(@RequestBody ParentSignupReqDto parentSignupReqDto) {

        parentService.join(parentSignupReqDto);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "부모 회원가입이 완료되었습니다.",
                        null
                )
        );
    }

    // 로그인, 로그아웃은 필터에서 구현하고 스웨거 문서에만 표시하기위해 작성---
    @Operation(summary = "부모 로그인", description = "부모 로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody ParentLoginReqDto parentLoginReqDto) {

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "부모 로그인이 완료되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "부모 parentId 찾기", description = "parentId를 찾습니다.")
    @GetMapping("/parentId")
    public ResponseEntity<ApiResponse<Integer>> findParentId(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "부모 parentId를 찾았습니다.",
                        customUserDetails.getParentId()
                )
        );

    }

}
