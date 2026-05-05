package com.travel.mapper;

import com.travel.pojo.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
            "FROM users WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    @Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
            "FROM users WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
            "FROM users WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);

    @Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
            "FROM users WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    @Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
            "FROM users WHERE username = #{account} OR phone = #{account} OR email = #{account} LIMIT 1")
    User selectByAccount(@Param("account") String account);

    @Insert("""
            INSERT INTO users (username, phone, email, password_hash, status)
            VALUES (#{username}, #{phone}, #{email}, #{passwordHash}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET last_login_at = NOW() WHERE id = #{id}")
    int updateLastLoginAt(@Param("id") Long id);
}
