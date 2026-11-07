package com.carrent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrent.common.BusinessException;
import com.carrent.common.PageResult;
import com.carrent.dto.ProfileRequest;
import com.carrent.dto.UserForm;
import com.carrent.entity.User;
import com.carrent.mapper.UserMapper;
import com.carrent.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户（客户）服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResult<User> adminPage(Integer current, Integer size, String keyword, Integer gender) {
        int cur = current == null || current < 1 ? 1 : current;
        int siz = size == null || size < 1 ? 10 : size;
        Page<User> page = new Page<>(cur, siz);
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.and(StringUtils.hasText(keyword), w -> w.like(User::getUsername, keyword)
                        .or().like(User::getName, keyword)
                        .or().like(User::getPhone, keyword))
                .eq(gender != null, User::getGender, gender)
                .orderByDesc(User::getId);
        Page<User> result = userMapper.selectPage(page, qw);
        result.getRecords().forEach(u -> u.setPassword(null));
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public void add(UserForm form) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, form.username()));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        applyForm(user, form);
        user.setPassword(passwordEncoder.encode(
                StringUtils.hasText(form.password()) ? form.password() : DEFAULT_PASSWORD));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMapper.insert(user);
    }

    @Override
    public void update(Long id, UserForm form) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("客户不存在");
        }
        if (!user.getUsername().equals(form.username())) {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, form.username()));
            if (count != null && count > 0) {
                throw new BusinessException("用户名已存在");
            }
        }
        applyForm(user, form);
        if (StringUtils.hasText(form.password())) {
            user.setPassword(passwordEncoder.encode(form.password()));
        }
        userMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("客户不存在");
        }
        userMapper.deleteById(id);
    }

    @Override
    public void updateProfile(Long userId, ProfileRequest req) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setName(req.name());
        user.setGender(req.gender());
        user.setBirthDate(req.birthDate());
        user.setIdCard(req.idCard());
        user.setPhone(req.phone());
        user.setDriveYears(req.driveYears());
        userMapper.updateById(user);
    }

    private void applyForm(User user, UserForm form) {
        user.setUsername(form.username());
        user.setName(form.name());
        user.setGender(form.gender() == null ? 1 : form.gender());
        user.setBirthDate(form.birthDate());
        user.setIdCard(form.idCard());
        user.setPhone(form.phone());
        user.setDriveYears(form.driveYears() == null ? 0 : form.driveYears());
        if (form.status() != null) {
            user.setStatus(form.status());
        }
    }
}
