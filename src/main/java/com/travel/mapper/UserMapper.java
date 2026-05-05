package com.travel.mapper;

import com.travel.pojo.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    @Select("SELECT id, username, phone, email, password_hash, status, last_login_at, created_at, updated_at " +
            "FROM users " +
            "<where>" +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</where>" +
            "ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<User> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM users " +
            "<where>" +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</where>")
    long countAll(@Param("status") Integer status);

    @Update("UPDATE users SET username=#{username}, phone=#{phone}, email=#{email}, status=#{status} WHERE id=#{id}")
    int updateUser(@Param("id") Long id, @Param("username") String username,
                   @Param("phone") String phone, @Param("email") String email,
                   @Param("status") Integer status);

    @Update("UPDATE users SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
