package com.stillalive.Ssook_BE.config;

//import com.stillalive.Ssook_BE.common.RefreshService;
import com.stillalive.Ssook_BE.filter.JWTFilter;
import com.stillalive.Ssook_BE.filter.LoginFilter;
import com.stillalive.Ssook_BE.filter.LogoutFilter;
import com.stillalive.Ssook_BE.user.service.CustomUserDetailsService;
import com.stillalive.Ssook_BE.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
//    private final RefreshService refreshService;

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        LoginFilter loginFilter = new LoginFilter(authenticationManager(), jwtUtil, refreshService);

        // 청소년 로그인 필터
        LoginFilter childLoginFilter = new LoginFilter(authenticationManager(), jwtUtil);
        childLoginFilter.setFilterProcessesUrl("/api/v1/child/login");

        // 부모 로그인 필터
        LoginFilter parentLoginFilter = new LoginFilter(authenticationManager(), jwtUtil);
        parentLoginFilter.setFilterProcessesUrl("/api/v1/parent/login");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리 설정
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint)) // 인증 예외 처리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/parent/login", "/api/v1/child/login", "/api/v1/parent/join", "/api/v1/child/join", "/api/v1/user/*").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/pay/view/point/charge").permitAll() // 임시용
                        .requestMatchers("/api/v1/school/search").permitAll() // 학교 검색
                        .requestMatchers("/api/v1/pay/nfc").permitAll() // NFC결제
                        .requestMatchers("/api/v1/alerts/subscribe").permitAll() // SSE 토큰 전달 불가함.
                        .requestMatchers("/js/**", "/favicon.ico", "/css/**").permitAll() // 정적 리소스에 대한 접근 허용(임시)
                        .anyRequest().authenticated())
//                .addFilterBefore(new JWTFilter(jwtUtil, customUserDetailsService, refreshService), LoginFilter.class)
                .addFilterBefore(new JWTFilter(jwtUtil, customUserDetailsService), LoginFilter.class)
                .addFilterAt(childLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(parentLoginFilter, UsernamePasswordAuthenticationFilter.class);
//                .addFilterBefore(new LogoutFilter(refreshService), org.springframework.security.web.authentication.logout.LogoutFilter.class);

        // LogoutFilter 추가
        http.addFilterBefore(new LogoutFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정 메서드
    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:5173", "https://ssook.duckdns.org/", "https://ssookssook.kr"));
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setMaxAge(3600L);
            configuration.setExposedHeaders(Collections.singletonList("Authorization"));
            return configuration;
        };
    }
}
