package com.carrent.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carrent.entity.Admin;
import com.carrent.entity.User;
import com.carrent.mapper.AdminMapper;
import com.carrent.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 启动时初始化默认账号（管理员 + 演示用户）
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(AdminMapper adminMapper, UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedUser();
    }

    private void seedAdmin() {
        Long count = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin"));
        if (count != null && count > 0) {
            return;
        }
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setName("系统管理员");
        admin.setRole("ADMIN");
        adminMapper.insert(admin);
        log.info("已创建默认管理员账号：admin / admin123");
    }

    private void seedUser() {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, "user1"));
        if (count != null && count > 0) {
            return;
        }
        User user = new User();
        user.setUsername("user1");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setName("张三");
        user.setGender(1);
        user.setBirthDate(LocalDate.of(1998, 5, 20));
        user.setIdCard("110101199805201234");
        user.setPhone("13800138000");
        user.setDriveYears(5);
        user.setStatus(1);
        userMapper.insert(user);
        log.info("已创建演示用户账号：user1 / 123456");
    }
}
