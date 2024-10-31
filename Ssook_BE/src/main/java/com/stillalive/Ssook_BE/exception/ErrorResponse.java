package com.stillalive.Ssook_BE.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class ErrorResponse {
    private final int statusCode; // 숫자 상태 코드 (예: 500, 404)
    private final String httpStatus; // 문자열 상태 코드 (예: INTERNAL_SERVER_ERROR, NOT_FOUND)
    private final String message; // 상태 메시지
    private final String errorDetails; // 에러의 구체적인 원인
}