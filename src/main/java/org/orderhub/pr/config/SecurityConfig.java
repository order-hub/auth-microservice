    package org.orderhub.pr.config;

    import lombok.RequiredArgsConstructor;
    import org.orderhub.pr.auth.service.MemberQueryService;
    import org.orderhub.pr.jwt.security.JwtAuthenticationFilter;
    import org.orderhub.pr.jwt.security.JwtService;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
    import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    import java.util.List;

    @Configuration
    @RequiredArgsConstructor
    public class SecurityConfig{
        private final JwtService jwtService;
        private final MemberQueryService memberQueryService;

        public static final String[] PERMITTED_URI = {"/api/auth/signup", "/api/auth/login"};

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }


        // cors 통해 허용할 도메인, 메서드, 헤더, 자격증명(쿠키), Pre-flight 요청의 캐시 시간 설정가능
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://frontend.com")); // 허용할 프론트엔드 주소
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
            config.setAllowedHeaders(List.of("*"));
            config.setAllowCredentials(true); // 백엔드가 쿠키를 사용할 수 있도록 허용함.
            config.setExposedHeaders(List.of("Authorization")); // 보안 헤더 접근용

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
        }

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .csrf(AbstractHttpConfigurer::disable) // jwt 기반 방식은 세션이 아닌 토큰 기반 방식이기 때문에 비활성화
                    .httpBasic(HttpBasicConfigurer::disable)
                    .formLogin(FormLoginConfigurer::disable)

                    // jwt를 사용할 것이기 때문에 세션 생성 정책을 STATELESS로 지정, 시큐리티는 기본적으로 세션을 생성하려고 함
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                    // JWT 검증 필터 추가
                    .addFilterBefore(new JwtAuthenticationFilter(jwtService, memberQueryService),
                            UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()
                            .anyRequest().authenticated()
                    );

            return http.build();
        }

    }
