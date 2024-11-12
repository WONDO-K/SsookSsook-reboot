package com.stillalive.Ssook_BE.user.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.dto.UserInfoResDto;
import com.stillalive.Ssook_BE.user.service.ChildService;
import com.stillalive.Ssook_BE.user.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User Controller", description = "유저 API")
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final ParentService parentService;
    private final ChildService childService;

    @Operation(summary = "로그인 아이디 중복 확인", description = "로그인 아이디 중복을 확인합니다")
    @GetMapping("/login-id")
    public ResponseEntity<ApiResponse<Void>> checkLoginId(@RequestParam String loginId) {
        if (parentService.existsByLoginId(loginId)) {
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (childService.existsByLoginId(loginId)) {
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

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

    @Operation(summary = "유저 정보 조회", description = "유저 정보를 조회합니다")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResDto>> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String loginId = customUserDetails.getUsername();
        log.info("loginId: {}", loginId);

        UserInfoResDto userInfoResDto = null;

        Boolean isParent = parentService.existsByLoginId(loginId);


        if (isParent) {
            userInfoResDto = parentService.getUserInfo(loginId);
        } else {
            userInfoResDto = childService.getUserInfo(loginId);
        }

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.getReasonPhrase(),
                        "유저 정보 조회 성공",
                        userInfoResDto
                )
        );
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "로그아웃이 완료되었습니다.",
                        null
                )
        );
    }

}
