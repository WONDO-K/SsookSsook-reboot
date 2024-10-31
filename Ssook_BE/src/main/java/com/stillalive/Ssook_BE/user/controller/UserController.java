package com.stillalive.Ssook_BE.user.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.user.service.ChildService;
import com.stillalive.Ssook_BE.user.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User Controller", description = "유저 API")
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final ParentService parentService;
    private final ChildService childService;

    @Operation(summary = "로그인 아이디 중복 확인", description = "로그인 아이디 중복을 확인합니다")
    @GetMapping("/login-id")
    public ResponseEntity<ApiResponse<Void>> checkLoginId(@RequestParam String loginId) {
        parentService.existsByLoginId(loginId);
        childService.existsByLoginId(loginId);

        // 예외가 발생하지 않으면 사용 가능한 아이디임
        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.getReasonPhrase(),
                        "사용 가능한 아이디입니다.",
                        null
                )
        );
    }
}
