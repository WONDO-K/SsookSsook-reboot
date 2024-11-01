package com.stillalive.Ssook_BE.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    NOT_EXIST_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "존재하지 않는 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT입니다."),
    EMPTY_ID_OR_PASSWORD(HttpStatus.BAD_REQUEST, "아이디 혹은 비밀번호가 비어있습니다."),
    INVALID_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "로그인 요청을 파싱하지 못했습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    NOT_EXIST_COOKIE(HttpStatus.UNAUTHORIZED, "쿠키에 값이 없습니다."),

    // User
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 로그인 아이디입니다."),
    DUPLICATE_TEL(HttpStatus.CONFLICT, "이미 존재하는 전화번호입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "없는 회원의 정보 입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),

    // Child
    NOT_FOUND_SCHOOL(HttpStatus.NOT_FOUND, "해당 학교를 찾을 수 없습니다."),
    NOT_FOUND_CHILD(HttpStatus.NOT_FOUND, "해당 아이를 찾을 수 없습니다."),

    // 카드 관련 에러 코드
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "카드를 찾을 수 없습니다."),
    DUPLICATED_CARD(HttpStatus.BAD_REQUEST, "이미 등록된 카드입니다."),
    CARD_EXPIRED(HttpStatus.BAD_REQUEST, "카드가 만료되었습니다."),
    CARD_REGISTRATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카드 등록 중 문제가 발생했습니다."),
    TOKENIZATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카드 정보 토큰화 중 문제가 발생했습니다."),

    // 잔액 관련 에러 코드
    BALANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "잔액을 조회할 수 없습니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),

    // 결제 관련 에러 코드
    PAYMENT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 문제가 발생했습니다."),
    HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 내역을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 메뉴 관련 에러 코드
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다.");

    // Parent
    NOT_FOUND_PARENT(HttpStatus.NOT_FOUND, "해당 부모를 찾을 수 없습니다."),

    // FamilyRelation
    NOT_FOUND_FAMILY_RELATION(HttpStatus.NOT_FOUND, "해당 가족 관계를 찾을 수 없습니다."),
    NOT_MY_FAMILY_RELATION(HttpStatus.FORBIDDEN, "본인의 가족 관계가 아닙니다."),


    ;
    private final HttpStatus httpstatus;
    private final String message;
}
