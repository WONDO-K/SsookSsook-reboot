package com.stillalive.Ssook_BE.filter;

//import com.stillalive.Ssook_BE.common.RefreshService;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LogoutFilter extends GenericFilterBean {

//    private final RefreshService refreshService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/api\\/v1\\/user\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        //로그아웃 요청 로그
        log.info("Logout request : POST /api/v1/user/logout");

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        //쿠키에 값이 없을 경우
        if (cookies == null) {
            //로그아웃 실패 로그
            log.error("Logout fail for: POST /api/v1/user/logout - 쿠키가 없습니다");
            throw new SsookException(ErrorCode.NOT_EXIST_COOKIE);
        }

        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        //refresh null check
        if (refresh == null) {
            //로그아웃 실패 로그
            log.error("Logout fail for: POST /api/v1/user/logout - 리프레쉬 토큰이 없습니다");
            throw new SsookException(ErrorCode.NOT_EXIST_REFRESH_TOKEN);
        }

//        //refresh token expired check
//        if(refreshService.isRefreshTokenExpired(refresh)) {
//            //로그아웃 실패 로그
//            log.error("Logout fail for: POST /api/v1/user/logout - 리프레쉬 토큰이 만료되었습니다");
//            throw new SsookException(ErrorCode.EXPIRED_REFRESH_TOKEN);
//        }
//
//        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
//        if (!refreshService.confirmRefreshToken(refresh)) {
//            //로그아웃 실패 로그
//            log.error("Logout fail for: POST /api/v1/user/logout - 리프레쉬 토큰이 아닙니다");
//            throw new SsookException(ErrorCode.INVALID_REFRESH_TOKEN);
//        }
//
//        //DB에 저장되어 있는지 확인
//        if (!refreshService.existsByRefresh(refresh)) {
//            //로그아웃 실패 로그
//            log.error("Logout fail for: POST /api/v1/user/logout - 레디스에 해당 리프레쉬 토큰이 없습니다");
//            throw new SsookException(ErrorCode.INVALID_REFRESH_TOKEN);
//        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
//        refreshService.deleteRefreshTokenByRefresh(refresh);

        //로그아웃 성공 로그
        log.info("Logout success for: POST /api/v1/user/logout");

        //Refresh 토큰 Cookie 초기화
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}


