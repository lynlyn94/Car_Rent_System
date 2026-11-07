package com.carrent.interceptor;

import com.carrent.common.Result;
import com.carrent.util.JwtUtil;
import com.carrent.util.ResponseUtils;
import com.carrent.util.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录鉴权拦截器：校验请求头中的 JWT，并将用户信息写入上下文
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            ResponseUtils.writeJson(response, 401, Result.error(401, "未登录或登录已过期"));
            return false;
        }
        try {
            Claims claims = jwtUtil.parseToken(header.substring(7));
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            UserContext.set(userId, username, role);
            return true;
        } catch (Exception e) {
            ResponseUtils.writeJson(response, 401, Result.error(401, "登录凭证无效或已过期"));
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
