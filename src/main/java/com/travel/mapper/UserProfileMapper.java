package com.travel.mapper;

import com.travel.pojo.model.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

    UserProfile selectByUserId(@Param("userId") Long userId);

    int insert(UserProfile profile);

    int updateProfile(UserProfile profile);
}
