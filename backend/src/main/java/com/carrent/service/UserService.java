package com.carrent.service;

import com.carrent.common.PageResult;
import com.carrent.dto.ProfileRequest;
import com.carrent.dto.UserForm;
import com.carrent.entity.User;

/**
 * 用户（客户）服务
 */
public interface UserService {

    PageResult<User> adminPage(Integer current, Integer size, String keyword, Integer gender);

    void add(UserForm form);

    void update(Long id, UserForm form);

    void delete(Long id);

    void updateProfile(Long userId, ProfileRequest req);
}
