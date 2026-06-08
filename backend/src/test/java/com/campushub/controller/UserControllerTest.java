package com.campushub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.campushub.common.Result;
import com.campushub.dto.user.UserProfileUpdateDTO;
import com.campushub.service.user.UserService;
import com.campushub.vo.UploadVO;
import com.campushub.vo.user.UserProfileVO;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // ==================== getCurrentUserProfile ====================

    @Test
    void getCurrentUserProfileShouldReturnProfile() throws Exception {
        UserProfileVO profile = new UserProfileVO();
        profile.setId(UUID.randomUUID().toString());
        profile.setUsername("testuser");
        profile.setNickname("测试用户");
        profile.setCreditScore(100);
        profile.setSchool("测试大学");
        profile.setInterests(List.of("编程", "篮球"));
        profile.setTags(List.of("Java", "Spring"));

        when(userService.getCurrentUserProfile("valid-token"))
                .thenReturn(Result.success(profile));

        mockMvc.perform(get("/api/users/me")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"))
                .andExpect(jsonPath("$.data.creditScore").value(100))
                .andExpect(jsonPath("$.data.school").value("测试大学"))
                .andExpect(jsonPath("$.data.interests[0]").value("编程"))
                .andExpect(jsonPath("$.data.tags[1]").value("Spring"));

        verify(userService).getCurrentUserProfile("valid-token");
    }

    @Test
    void getCurrentUserProfileShouldReturn401WhenUnauthorized() throws Exception {
        when(userService.getCurrentUserProfile(null))
                .thenReturn(Result.error(401, "请先登录"));

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void getCurrentUserProfileShouldReturn404WhenUserNotFound() throws Exception {
        when(userService.getCurrentUserProfile("valid-token"))
                .thenReturn(Result.error(404, "用户不存在"));

        mockMvc.perform(get("/api/users/me")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void getCurrentUserProfileShouldReturn401WhenTokenIsBlank() throws Exception {
        when(userService.getCurrentUserProfile(""))
                .thenReturn(Result.error(401, "请先登录"));

        mockMvc.perform(get("/api/users/me")
                        .header("token", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    // ==================== updateCurrentUserProfile ====================

    @Test
    void updateCurrentUserProfileShouldSucceed() throws Exception {
        UserProfileVO profile = new UserProfileVO();
        profile.setId(UUID.randomUUID().toString());
        profile.setUsername("testuser");
        profile.setNickname("新昵称");
        profile.setBio("这是我的个人简介");
        profile.setSchool("新学校");

        when(userService.updateCurrentUserProfile(eq("valid-token"), any(UserProfileUpdateDTO.class)))
                .thenReturn(Result.success(profile));

        mockMvc.perform(patch("/api/users/me")
                        .header("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"新昵称\",\"bio\":\"这是我的个人简介\",\"school\":\"新学校\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("新昵称"))
                .andExpect(jsonPath("$.data.bio").value("这是我的个人简介"))
                .andExpect(jsonPath("$.data.school").value("新学校"));

        verify(userService).updateCurrentUserProfile(eq("valid-token"), any(UserProfileUpdateDTO.class));
    }

    @Test
    void updateCurrentUserProfileShouldReturn401WhenUnauthorized() throws Exception {
        when(userService.updateCurrentUserProfile(isNull(), any(UserProfileUpdateDTO.class)))
                .thenReturn(Result.error(401, "请先登录"));

        mockMvc.perform(patch("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"新昵称\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void updateCurrentUserProfileShouldHandleNullBody() throws Exception {
        UserProfileVO profile = new UserProfileVO();
        profile.setId(UUID.randomUUID().toString());
        profile.setUsername("testuser");

        when(userService.updateCurrentUserProfile(eq("valid-token"), any(UserProfileUpdateDTO.class)))
                .thenReturn(Result.success(profile));

        mockMvc.perform(patch("/api/users/me")
                        .header("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateCurrentUserProfileShouldHandleAllFields() throws Exception {
        UserProfileVO profile = new UserProfileVO();
        profile.setId(UUID.randomUUID().toString());
        profile.setUsername("testuser");
        profile.setNickname("昵称");
        profile.setPhone("13800138000");
        profile.setEmail("test@example.com");
        profile.setBio("bio");
        profile.setSchool("school");
        profile.setMajor("CS");
        profile.setGrade("大三");
        profile.setInterests(List.of("coding"));
        profile.setTags(List.of("Java"));

        when(userService.updateCurrentUserProfile(eq("valid-token"), any(UserProfileUpdateDTO.class)))
                .thenReturn(Result.success(profile));

        mockMvc.perform(patch("/api/users/me")
                        .header("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"昵称\",\"phone\":\"13800138000\",\"email\":\"test@example.com\","
                                + "\"bio\":\"bio\",\"school\":\"school\",\"major\":\"CS\",\"grade\":\"大三\","
                                + "\"interests\":[\"coding\"],\"tags\":[\"Java\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("13800138000"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.major").value("CS"))
                .andExpect(jsonPath("$.data.grade").value("大三"));
    }

    // ==================== uploadAvatar ====================

    @Test
    void uploadAvatarShouldReturnUrl() throws Exception {
        UploadVO uploadVO = new UploadVO("/api/uploads/avatars/mock-123-avatar.jpg");
        when(userService.uploadAvatar(eq("valid-token"), any()))
                .thenReturn(Result.success(uploadVO));

        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "fake-image".getBytes());

        mockMvc.perform(multipart("/api/users/me/avatar")
                        .file(file)
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.url").value("/api/uploads/avatars/mock-123-avatar.jpg"));

        verify(userService).uploadAvatar(eq("valid-token"), any());
    }

    @Test
    void uploadAvatarShouldReturn401WhenUnauthorized() throws Exception {
        when(userService.uploadAvatar(isNull(), any()))
                .thenReturn(Result.error(401, "请先登录"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "fake-image".getBytes());

        mockMvc.perform(multipart("/api/users/me/avatar")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }
}
