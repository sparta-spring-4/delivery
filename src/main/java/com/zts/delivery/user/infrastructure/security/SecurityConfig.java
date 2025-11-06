package com.zts.delivery.user.infrastructure.security;


import com.zts.delivery.user.domain.UserRole;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtToUserAuthenticationConverter customConverter
                = new JwtToUserAuthenticationConverter(new JwtToUserRoleConverter());

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v1/users/token/**", "/v1/users/signup/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(c -> c
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customConverter))
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));
        return http.build();
    }

    // ROLE_MASTER > ROLE_MANAGER > ROLE_OWNER > ROLE_CUSTOMER
    // 앞쪽에 있는 ROLE이 상위 ROLE 이다.
    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = Stream.of(UserRole.MASTER, UserRole.MANAGER, UserRole.OWNER, UserRole.CUSTOMER)
                .map(role -> "ROLE_" + role.name())
                .collect(Collectors.joining(" > "));
        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    /**
     * 운영(prod) 환경 보안 설정
     */
    @Profile("!local")
    @Bean
    public WebSecurityCustomizer defaultWebSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) // 정적 리소스 무시
                .requestMatchers("/api-docs/**", "/swagger-ui/**"); // swagger-ui 접근 무시
    }

    /**
     * 개발(local) 환경 보안 설정
     */
    @Profile("local")
    @Bean
    public WebSecurityCustomizer localWebSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) // 정적 리소스 무시
                .requestMatchers(PathRequest.toH2Console()) // H2 콘솔 접근 무시 (개발용)
                .requestMatchers("/api-docs/**", "/swagger-ui/**"); // swagger-ui 접근 무시
    }
}