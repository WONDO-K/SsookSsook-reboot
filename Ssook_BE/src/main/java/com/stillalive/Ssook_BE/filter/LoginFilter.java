package com.stillalive.Ssook_BE.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.stillalive.Ssook_BE.common.RefreshService;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
//    private final RefreshService refreshService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 청소년 로그인 요청 로그
        log.info("Login request : POST /api/v1/child/login");

        String loginId = null;
        String password = null;

        try {
            // Content-Type을 확인하여 처리
            String contentType = request.getContentType();

            // 1. Form Data 처리
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                //클라이언트 요청에서 username, password 추출
                loginId = request.getParameter("loginId");
                password = request.getParameter("password");

            }

            // 2. Raw JSON 데이터 처리
            else if (contentType != null && contentType.equals("application/json")) {
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                // JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(sb.toString());
                loginId = jsonNode.get("loginId").asText();
                password = jsonNode.get("password").asText();
            }

            // 두 방식 중 어느 쪽에서든 loginId와 password를 가져온 후 처리
            if (loginId != null && password != null) {
                // 스프링 시큐리티에서 검증을 위해 token에 담아야 함
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, password, null);

                // AuthenticationManager로 전달
                return authenticationManager.authenticate(authToken);
            } else {
                throw new SsookException(ErrorCode.EMPTY_ID_OR_PASSWORD);
            }

        } catch (IOException e) {
            throw new SsookException(ErrorCode.INVALID_LOGIN_REQUEST);
        }
    }


    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String loginId = customUserDetails.getUsername();

        String access = jwtUtil.createJwt("access", loginId, 86300000L);
        String refresh = jwtUtil.createJwt("refresh", loginId, 86400000L);

        //Refresh 토큰 저장
//        refreshService.saveRefreshToken(refresh);

        // 로그인 성공 로그
        log.info("Login success for: POST /api/v1/child/login");

        //응답 설정
        response.addHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        // 로그인 실패 로그
        log.info("Login fail for: POST /api/v1/child/login",failed.getMessage());

        throw new SsookException(ErrorCode.LOGIN_FAILED);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(86400);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}