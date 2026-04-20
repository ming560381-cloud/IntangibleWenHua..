package com.feiyi.feiyiwenhua.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173"); // 允许的源地址，这里假设前端运行在这个地址
        config.addAllowedOrigin("http://localhost:5174"); // 允许前端运行在5174端口
        config.addAllowedMethod("*"); // 允许的 HTTP 方法，这里允许所有方法
        config.addAllowedHeader("*"); // 允许的请求头，这里允许所有请求头
        config.setAllowCredentials(true); // 是否允许携带凭证（如 cookies）
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}