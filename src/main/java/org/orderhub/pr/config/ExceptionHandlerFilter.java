package org.orderhub.pr.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.orderhub.pr.auth.exception.ExceptionMessage.INVALID_JWT;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws IOException {

        try {
            // 다음 필터로 요청을 전달
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Exception caught in ExceptionHandlerFilter", ex);

            // 예외 처리 로직 (특정 예외를 잡아서 응답 반환)
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"message\": \"%s\"}", INVALID_JWT));
        }
    }
}