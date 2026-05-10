package com.travel.mapper;

import com.travel.pojo.model.AdminUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

    @Select("SELECT * FROM admin_users ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<AdminUser> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM admin_users")
    long countAll();

    @Update("UPDATE admin_users SET real_name=#{realName}, status=#{status} WHERE id=#{id}")
    int updateAdmin(@Param("id") Long id, @Param("realName") String realName, @Param("status") Integer status);

    @Update("UPDATE admin_users SET password_hash=#{passwordHash} WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

    @Delete("DELETE FROM admin_users WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
