package com.stillalive.Ssook_BE.user.controller;

import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.dto.ChildLoginRequestDto;
import com.stillalive.Ssook_BE.user.dto.ChildSignupRequestDto;
import com.stillalive.Ssook_BE.user.service.ChildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Child Controller", description = "자녀 API")
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @Operation(summary = "청소년 회원가입", description = "청소년 회원가입을 진행합니다.")
    @PostMapping("/join")
    public void join(@RequestBody ChildSignupRequestDto childSignupRequestDto) {

        childService.join(childSignupRequestDto);
    }

    // 로그인, 로그아웃은 필터에서 구현하고 스웨거 문서에만 표시하기위해 작성---
    @Operation(summary = "청소년 로그인", description = "청소년 로그인을 진행합니다.")
    @PostMapping("/login")
    public String login(@RequestBody ChildLoginRequestDto childLoginRequestDto) {
        return "child login";
    }

    @Operation(summary = "청소년 childId 찾기", description = "childId를 찾습니다.")
    @GetMapping("/childId")
    public Integer findChildId(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("customUserDetails.getChildId() = " + customUserDetails.getChildId());
        return customUserDetails.getChildId(); // childId 반환
    }

}
