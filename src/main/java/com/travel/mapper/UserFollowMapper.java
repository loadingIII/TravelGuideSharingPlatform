package com.travel.mapper;

import com.travel.pojo.model.UserProfile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserFollowMapper {

    @Select("SELECT COUNT(*) FROM user_follows WHERE follower_user_id=#{followerUserId} AND followed_user_id=#{followedUserId}")
    int exists(@Param("followerUserId") Long followerUserId, @Param("followedUserId") Long followedUserId);

    @Insert("INSERT INTO user_follows (follower_user_id, followed_user_id) VALUES (#{followerUserId}, #{followedUserId})")
    int insert(@Param("followerUserId") Long followerUserId, @Param("followedUserId") Long followedUserId);

    @Delete("DELETE FROM user_follows WHERE follower_user_id=#{followerUserId} AND followed_user_id=#{followedUserId}")
    int delete(@Param("followerUserId") Long followerUserId, @Param("followedUserId") Long followedUserId);

    @Update("UPDATE user_profiles SET followers_count = followers_count + 1 WHERE user_id = #{userId}")
    int incrementFollowersCount(@Param("userId") Long userId);

    @Update("UPDATE user_profiles SET followers_count = followers_count - 1 WHERE user_id = #{userId} AND followers_count > 0")
    int decrementFollowersCount(@Param("userId") Long userId);

    @Select("SELECT followers_count FROM user_profiles WHERE user_id = #{userId}")
    long selectFollowersCount(@Param("userId") Long userId);

    @Select("SELECT up.user_id, up.nickname, up.avatar_url, up.is_vip " +
            "FROM user_follows uf " +
            "JOIN user_profiles up ON uf.follower_user_id = up.user_id " +
            "WHERE uf.followed_user_id = #{userId} " +
            "ORDER BY uf.created_at DESC")
    List<UserProfile> selectFollowers(@Param("userId") Long userId);

    @Select("SELECT up.user_id, up.nickname, up.avatar_url, up.is_vip " +
            "FROM user_follows uf " +
            "JOIN user_profiles up ON uf.followed_user_id = up.user_id " +
            "WHERE uf.follower_user_id = #{userId} " +
            "ORDER BY uf.created_at DESC")
    List<UserProfile> selectFollowing(@Param("userId") Long userId);
}
