package com.campushub.service.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.campushub.common.Result;
import com.campushub.dto.user.UserProfileUpdateDTO;
import com.campushub.entity.User;
import com.campushub.entity.UserDetail;
import com.campushub.mapper.UsersMapper;
import com.campushub.service.auth.AuthService;
import com.campushub.vo.UploadVO;
import com.campushub.vo.user.UserProfileVO;

@Service
public class UserService {
    private final UsersMapper usersMapper;
    private final AuthService authService;

    public UserService(UsersMapper usersMapper, AuthService authService) {
        this.usersMapper = usersMapper;
        this.authService = authService;
    }

    public Result<UserProfileVO> getCurrentUserProfile(String token) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        User user = usersMapper.selectUserByUuid(userUuid);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        UserDetail detail = ensureUserDetail(user);
        return Result.success(toVO(user, detail));
    }

    public Result<UserProfileVO> updateCurrentUserProfile(String token, UserProfileUpdateDTO dto) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        User user = usersMapper.selectUserByUuid(userUuid);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        UserDetail detail = ensureUserDetail(user);
        if (dto != null) {
            detail.setNickname(dto.getNickname());
            detail.setPhone(dto.getPhone());
            detail.setEmail(dto.getEmail());
            detail.setAvatarUrl(dto.getAvatarUrl());
            detail.setBio(dto.getBio());
            detail.setSchool(dto.getSchool());
            detail.setMajor(dto.getMajor());
            detail.setGrade(dto.getGrade());
            detail.setInterests(dto.getInterests());
            detail.setTags(dto.getTags());
        }
        detail.setUpdateTime(LocalDateTime.now());
        usersMapper.updateUserDetail(detail);

        return Result.success(toVO(user, detail));
    }

    public Result<UploadVO> uploadAvatar(String token, MultipartFile file) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        String filename = file == null || file.getOriginalFilename() == null
                ? "avatar"
                : file.getOriginalFilename().replace("\\", "_").replace("/", "_");
        return Result.success(new UploadVO("/api/uploads/avatars/mock-" + UUID.randomUUID() + "-" + filename));
    }

    private UserDetail ensureUserDetail(User user) {
        UserDetail detail = usersMapper.selectUserDetailByUuid(user.getUuid());
        if (detail != null) {
            return detail;
        }

        detail = new UserDetail();
        detail.setUserUuid(user.getUuid());
        detail.setNickname(user.getUsername());
        detail.setCreditScore(100);
        detail.setCreateTime(LocalDateTime.now());
        detail.setUpdateTime(LocalDateTime.now());
        usersMapper.insertUserDetail(detail);
        return detail;
    }

    private UserProfileVO toVO(User user, UserDetail detail) {
        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getUuid().toString());
        vo.setUsername(user.getUsername());
        vo.setNickname(detail.getNickname());
        vo.setPhone(detail.getPhone());
        vo.setEmail(detail.getEmail());
        vo.setAvatarUrl(detail.getAvatarUrl());
        vo.setCreditScore(detail.getCreditScore());
        vo.setBio(detail.getBio());
        vo.setSchool(detail.getSchool());
        vo.setMajor(detail.getMajor());
        vo.setGrade(detail.getGrade());
        vo.setInterests(detail.getInterests());
        vo.setTags(detail.getTags());
        return vo;
    }
}
