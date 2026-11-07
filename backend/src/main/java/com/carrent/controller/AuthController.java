package com.carrent.controller;

import com.carrent.common.Result;
import com.carrent.dto.LoginRequest;
import com.carrent.vo.LoginVO;
import com.carrent.dto.RegisterRequest;
import com.carrent.entity.User;
import com.carrent.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterRequest req) {
        return Result.success("注册成功", authService.register(req));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest req) {
        return Result.success("登录成功", authService.login(req));
    }

    @PostMapping("/admin/login")
    public Result<LoginVO> adminLogin(@Valid @RequestBody LoginRequest req) {
        return Result.success("登录成功", authService.adminLogin(req));
    }

    @GetMapping("/me")
    public Result<User> me() {
        return Result.success(authService.me());
    }
}
