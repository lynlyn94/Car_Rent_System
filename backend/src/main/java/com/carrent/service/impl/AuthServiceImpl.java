package com.carrent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carrent.common.BusinessException;
import com.carrent.dto.LoginRequest;
import com.carrent.vo.LoginVO;
import com.carrent.dto.RegisterRequest;
import com.carrent.entity.Admin;
import com.carrent.entity.User;
import com.carrent.mapper.AdminMapper;
import com.carrent.mapper.UserMapper;
import com.carrent.service.AuthService;
import com.carrent.util.JwtUtil;
import com.carrent.util.UserContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";

    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper, AdminMapper adminMapper,
                           BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginVO register(RegisterRequest req) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.username()));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setName(req.name());
        user.setGender(req.gender() == null ? 1 : req.gender());
        user.setBirthDate(req.birthDate());
        user.setIdCard(req.idCard());
        user.setPhone(req.phone());
        user.setDriveYears(req.driveYears() == null ? 0 : req.driveYears());
        user.setStatus(1);
        userMapper.insert(user);
        return buildLoginVO(user.getId(), user.getUsername(), user.getName(), ROLE_USER);
    }

    @Override
    public LoginVO login(LoginRequest req) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.username()));
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        return buildLoginVO(user.getId(), user.getUsername(), user.getName(), ROLE_USER);
    }

    @Override
    public LoginVO adminLogin(LoginRequest req) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, req.username()));
        if (admin == null || !passwordEncoder.matches(req.password(), admin.getPassword())) {
            throw new BusinessException("管理员用户名或密码错误");
        }
        return buildLoginVO(admin.getId(), admin.getUsername(), admin.getName(), ROLE_ADMIN);
    }

    @Override
    public User me() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    private LoginVO buildLoginVO(Long id, String username, String name, String role) {
        String token = jwtUtil.generateToken(id, username, role);
        return new LoginVO(token, id, username, name, role);
    }
}
