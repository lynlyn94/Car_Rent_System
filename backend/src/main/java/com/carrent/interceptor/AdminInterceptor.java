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
 * 管理员鉴权拦截器：校验 JWT 并校验角色为 ADMIN
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    public static final String ROLE_ADMIN = "ADMIN";

    private final JwtUtil jwtUtil;

    public AdminInterceptor(JwtUtil jwtUtil) {
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
            String role = claims.get("role", String.class);
            if (!ROLE_ADMIN.equals(role)) {
                ResponseUtils.writeJson(response, 403, Result.error(403, "无管理员权限"));
                return false;
            }
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
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
