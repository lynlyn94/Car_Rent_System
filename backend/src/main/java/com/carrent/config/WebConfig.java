package com.carrent.config;

import com.carrent.interceptor.AdminInterceptor;
import com.carrent.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web MVC 配置：拦截器注册、静态资源映射与跨域配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public WebConfig(AuthInterceptor authInterceptor, AdminInterceptor adminInterceptor) {
        this.authInterceptor = authInterceptor;
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 需要登录（用户）的接口
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/api/orders/**",
                        "/api/messages/**",
                        "/api/auth/me",
                        "/api/auth/profile"
                );
        // 需要管理员权限的接口
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 映射到本地文件目录，使上传的图片可被浏览器访问
        String location = "file:" + Paths.get(uploadDir).toAbsolutePath().normalize() + "/";
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
