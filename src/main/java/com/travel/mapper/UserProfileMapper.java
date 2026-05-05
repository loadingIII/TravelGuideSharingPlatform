package com.travel.mapper;

import com.travel.pojo.model.UserProfile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserProfileMapper {

    @Select("SELECT user_id, nickname, avatar_url, bio, is_vip, guides_count, followers_count, likes_received_count, created_at, updated_at FROM user_profiles WHERE user_id = #{userId}")
    UserProfile selectByUserId(@Param("userId") Long userId);

    @Insert("""
            INSERT INTO user_profiles (user_id, nickname, avatar_url, bio, is_vip)
            VALUES (#{userId}, #{nickname}, #{avatarUrl}, #{bio}, #{isVip})
            """)
    int insert(UserProfile profile);

    @Update("""
            UPDATE user_profiles
            SET nickname = #{nickname},
                avatar_url = #{avatarUrl},
                bio = #{bio}
            WHERE user_id = #{userId}
            """)
    int updateProfile(UserProfile profile);
}
