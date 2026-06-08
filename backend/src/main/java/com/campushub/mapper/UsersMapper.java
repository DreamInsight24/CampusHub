package com.campushub.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;

import com.campushub.entity.User;
import com.campushub.entity.UserDetail;

public interface UsersMapper {
    //需要的接口自行补充
    int insertUser(User user);

    int insertUserDetail(UserDetail userDetail);

    int updateUser(User user);

    int updateUserDetail(UserDetail userDetail);

    int deleteUserByUuid(@Param("uuid") UUID uuid);

    User selectUserByUuid(@Param("uuid") UUID uuid);

    User selectUserByUsername(@Param("username") String username);

    UserDetail selectUserDetailByUuid(@Param("uuid") UUID uuid);

    List<User> selectUsersByNickname(@Param("nickname") String nickname);

    List<User> selectAllUsers();
}
