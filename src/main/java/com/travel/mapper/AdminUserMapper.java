package com.travel.mapper;

import com.travel.pojo.model.AdminUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminUserMapper {

    @Select("SELECT * FROM admin_users WHERE username = #{username}")
    AdminUser selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM admin_users WHERE id = #{id}")
    AdminUser selectById(@Param("id") Long id);

    @Update("UPDATE admin_users SET last_login_at = NOW() WHERE id = #{id}")
    int updateLastLoginAt(@Param("id") Long id);

    @Insert("""
            INSERT INTO admin_users (username, password_hash, real_name, status)
            VALUES (#{username}, #{passwordHash}, #{realName}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminUser adminUser);
}
