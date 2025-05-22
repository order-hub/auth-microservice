package org.orderhub.pr.config;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.config.resolver.CurrentMemberIdArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {
    private final CurrentMemberIdArgumentResolver ArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(ArgumentResolver);
    }
}
