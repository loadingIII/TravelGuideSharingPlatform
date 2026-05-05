package com.travel.config;

import com.travel.mapper.AdminUserMapper;
import com.travel.pojo.model.AdminUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final AdminUserMapper adminUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        AdminUser admin = adminUserMapper.selectByUsername("admin");
        if (admin == null) {
            AdminUser newAdmin = new AdminUser();
            newAdmin.setUsername("admin");
            newAdmin.setPasswordHash(passwordEncoder.encode("admin123"));
            newAdmin.setRealName("超级管理员");
            newAdmin.setStatus(1);
            adminUserMapper.insert(newAdmin);
            log.info("默认管理员账号已创建: admin / admin123");
        }
    }
}
