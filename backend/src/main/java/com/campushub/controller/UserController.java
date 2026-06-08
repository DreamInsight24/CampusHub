package com.campushub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campushub.common.Result;
import com.campushub.dto.user.UserProfileUpdateDTO;
import com.campushub.service.user.UserService;
import com.campushub.vo.UploadVO;
import com.campushub.vo.user.UserProfileVO;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<UserProfileVO> getCurrentUserProfile(
            @RequestHeader(value = "token", required = false) String token) {
        return userService.getCurrentUserProfile(token);
    }

    @PatchMapping("/me")
    public Result<UserProfileVO> updateCurrentUserProfile(
            @RequestHeader(value = "token", required = false) String token,
            @RequestBody UserProfileUpdateDTO dto) {
        return userService.updateCurrentUserProfile(token, dto);
    }

    @PostMapping("/me/avatar")
    public Result<UploadVO> uploadAvatar(
            @RequestHeader(value = "token", required = false) String token,
            @RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(token, file);
    }
}
