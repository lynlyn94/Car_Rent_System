package com.carrent.service;

import com.carrent.dto.LoginRequest;
import com.carrent.vo.LoginVO;
import com.carrent.dto.RegisterRequest;
import com.carrent.entity.User;

/**
 * 认证服务
 */
public interface AuthService {

    LoginVO register(RegisterRequest req);

    LoginVO login(LoginRequest req);

    LoginVO adminLogin(LoginRequest req);

    User me();
}
