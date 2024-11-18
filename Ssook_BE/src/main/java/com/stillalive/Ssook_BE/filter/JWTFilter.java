package com.stillalive.Ssook_BE.filter;

//import com.stillalive.Ssook_BE.common.RefreshService;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
//    private final RefreshService refreshService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        if (jwtUtil.isExpired(token)) {
            String refresh = getRefreshTokenFromCookies(request);

//            if (refresh == null || refreshService.isRefreshTokenExpired(refresh)) {
//                throw new SsookException(ErrorCode.EXPIRED_ACCESS_TOKEN);
//            }

            String loginId = jwtUtil.getUsername(refresh);
            token = jwtUtil.createJwt("access", loginId, 86300000L);
            String newRefresh = jwtUtil.createJwt("refresh", loginId, 86400000L);

//            refreshService.updateRefreshToken(refresh, newRefresh);

            response.setHeader("Authorization", "Bearer " + token);
            response.addCookie(createCookie("refresh", newRefresh));
        }

        String loginId = jwtUtil.getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        filterChain.doFilter(request, response);
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
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
