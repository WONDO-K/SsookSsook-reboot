package com.stillalive.Ssook_BE.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class ApiResponse<T> {
    private final int statusCode; // 숫자 상태 코드 (예: 200, 404)
    private final String httpStatus; // 문자열 상태 코드 (예: OK, NOT_FOUND)
    private final String message; // 상태 메시지
    private final T data; // 실제 응답 데이터
}
