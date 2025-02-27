package org.orderhub.pr.config;

import org.orderhub.pr.auth.domain.MemberRole;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화 (필요에 따라 변경)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/login/**", "/auth/").permitAll() // 로그인과 인증 페이지만 허용
                                .anyRequest().permitAll() // 나머지 요청은 인증된 사용자만 허용
                );
        return http.build();
    }

//    @Bean
//    static RoleHierarchy roleHierarchy() {
//        return RoleHierarchyImpl.withDefaultRolePrefix()
//                .role("ADMIN").implies("BOSS", "MANAGER")
//                .role("BOSS").implies("MANAGER")
//                .build();
//    }
}