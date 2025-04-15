package com.lukefowles.weather_rest_api;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class RateLimitInterceptorRegistry implements WebMvcConfigurer {

    private RateLimiter interceptor;

    public RateLimitInterceptorRegistry(RateLimiter interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/weather");
    }
}
