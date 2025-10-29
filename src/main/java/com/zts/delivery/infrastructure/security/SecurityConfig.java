package com.zts.delivery.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)      // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 페이지 제거
                .httpBasic(AbstractHttpConfigurer::disable) // 브라우저 인증 팝업 제거
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()           // 모든 요청 허용
                );
        return http.build(); // SecurityFilterChain 반환
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}