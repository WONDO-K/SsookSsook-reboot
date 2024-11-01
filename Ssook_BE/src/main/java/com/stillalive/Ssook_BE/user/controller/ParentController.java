package com.stillalive.Ssook_BE.user.controller;

import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.dto.ChildLoginRequestDto;
import com.stillalive.Ssook_BE.user.dto.ChildSignupRequestDto;
import com.stillalive.Ssook_BE.user.dto.ParentLoginRequestDto;
import com.stillalive.Ssook_BE.user.dto.ParentSignupRequestDto;
import com.stillalive.Ssook_BE.user.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public void join(@RequestBody ParentSignupRequestDto parentSignupRequestDto) {

        parentService.join(parentSignupRequestDto);
    }

    // 로그인, 로그아웃은 필터에서 구현하고 스웨거 문서에만 표시하기위해 작성---
    @Operation(summary = "부모 로그인", description = "부모 로그인을 진행합니다.")
    @PostMapping("/login")
    public String login(@RequestBody ParentLoginRequestDto parentLoginRequestDto) {
        return "parent login";
    }

    @Operation(summary = "부모 parentId 찾기", description = "parentId를 찾습니다.")
    @GetMapping("/parentId")
    public Integer findParentId(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("customUserDetails.getParentId() = " + customUserDetails.getParentId());
        return customUserDetails.getParentId(); // childId 반환
    }

}
