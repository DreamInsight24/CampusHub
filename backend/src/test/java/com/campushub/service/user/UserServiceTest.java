package com.campushub.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import com.campushub.common.Result;
import com.campushub.dto.user.UserProfileUpdateDTO;
import com.campushub.entity.User;
import com.campushub.entity.UserDetail;
import com.campushub.mapper.UsersMapper;
import com.campushub.service.auth.AuthService;
import com.campushub.vo.UploadVO;
import com.campushub.vo.user.UserProfileVO;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    private UUID userUuid;
    private String validToken;
    private User user;
    private UserDetail userDetail;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        validToken = "valid-token";

        user = new User();
        user.setUuid(userUuid);
        user.setUsername("testuser");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userDetail = new UserDetail();
        userDetail.setUserUuid(userUuid);
        userDetail.setNickname("昵称");
        userDetail.setCreditScore(100);
        userDetail.setSchool("测试大学");
        userDetail.setMajor("计算机科学");
        userDetail.setGrade("大三");
        userDetail.setInterests(List.of("编程"));
        userDetail.setTags(List.of("Java"));
        userDetail.setCreateTime(LocalDateTime.now());
        userDetail.setUpdateTime(LocalDateTime.now());

        when(authService.getUserUuidByToken(validToken)).thenReturn(userUuid);
    }

    // ==================== getCurrentUserProfile ====================

    @Nested
    @DisplayName("getCurrentUserProfile Tests")
    class GetCurrentUserProfileTests {

        @Test
        @DisplayName("should return user profile for valid token when detail exists")
        void getProfileSuccessWithExistingDetail() {
            when(usersMapper.selectUserByUuid(userUuid)).thenReturn(user);
            when(usersMapper.selectUserDetailByUuid(userUuid)).thenReturn(userDetail);

            Result<UserProfileVO> result = userService.getCurrentUserProfile(validToken);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(userUuid.toString(), result.getData().getId());
            assertEquals("testuser", result.getData().getUsername());
            assertEquals("昵称", result.getData().getNickname());
            assertEquals(100, result.getData().getCreditScore());
            assertEquals("测试大学", result.getData().getSchool());
            assertEquals("计算机科学", result.getData().getMajor());
            assertEquals("大三", result.getData().getGrade());
            assertEquals(List.of("编程"), result.getData().getInterests());
            assertEquals(List.of("Java"), result.getData().getTags());

            verify(usersMapper).selectUserByUuid(userUuid);
            verify(usersMapper).selectUserDetailByUuid(userUuid);
        }

        @Test
        @DisplayName("should create default UserDetail when it does not exist")
        void getProfileCreatesDetailWhenMissing() {
            when(usersMapper.selectUserByUuid(userUuid)).thenReturn(user);
            when(usersMapper.selectUserDetailByUuid(userUuid)).thenReturn(null);

            Result<UserProfileVO> result = userService.getCurrentUserProfile(validToken);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("testuser", result.getData().getUsername());
            assertEquals("testuser", result.getData().getNickname()); // defaults to username
            assertEquals(100, result.getData().getCreditScore()); // default credit score

            ArgumentCaptor<UserDetail> captor = ArgumentCaptor.forClass(UserDetail.class);
            verify(usersMapper).insertUserDetail(captor.capture());
            assertEquals(userUuid, captor.getValue().getUserUuid());
            assertEquals("testuser", captor.getValue().getNickname());
            assertEquals(100, captor.getValue().getCreditScore());
        }

        @Test
        @DisplayName("should return 401 when token is null")
        void getProfileNullToken() {
            when(authService.getUserUuidByToken(null)).thenReturn(null);

            Result<UserProfileVO> result = userService.getCurrentUserProfile(null);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
            verify(usersMapper, never()).selectUserByUuid(any());
        }

        @Test
        @DisplayName("should return 401 when token is blank")
        void getProfileBlankToken() {
            when(authService.getUserUuidByToken("   ")).thenReturn(null);

            Result<UserProfileVO> result = userService.getCurrentUserProfile("   ");

            assertEquals(401, result.getCode());
            verify(usersMapper, never()).selectUserByUuid(any());
        }

        @Test
        @DisplayName("should return 401 when token is invalid")
        void getProfileInvalidToken() {
            when(authService.getUserUuidByToken("bad-token")).thenReturn(null);

            Result<UserProfileVO> result = userService.getCurrentUserProfile("bad-token");

            assertEquals(401, result.getCode());
            verify(usersMapper, never()).selectUserByUuid(any());
        }

        @Test
        @DisplayName("should return 404 when user not found in database")
        void getProfileUserNotFound() {
            when(usersMapper.selectUserByUuid(userUuid)).thenReturn(null);

            Result<UserProfileVO> result = userService.getCurrentUserProfile(validToken);

            assertEquals(404, result.getCode());
            assertEquals("用户不存在", result.getMessage());
            assertNull(result.getData());
        }
    }

    // ==================== updateCurrentUserProfile ====================

    @Nested
    @DisplayName("updateCurrentUserProfile Tests")
    class UpdateCurrentUserProfileTests {

        @Test
        @DisplayName("should update profile fields successfully")
        void updateProfileSuccess() {
            when(usersMapper.selectUserByUuid(userUuid)).thenReturn(user);
            when(usersMapper.selectUserDetailByUuid(userUuid)).thenReturn(userDetail);

            UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
            dto.setNickname("新昵称");
            dto.setPhone("13800138000");
            dto.setEmail("newemail@test.com");
            dto.setBio("新的个人简介");
            dto.setSchool("新学校");
            dto.setMajor("软件工程");
            dto.setGrade("大四");
            dto.setInterests(List.of("编程", "摄影"));
            dto.setTags(List.of("Spring", "Vue"));

            Result<UserProfileVO> result = userService.updateCurrentUserProfile(validToken, dto);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("新昵称", result.getData().getNickname());
            assertEquals("13800138000", result.getData().getPhone());
            assertEquals("newemail@test.com", result.getData().getEmail());
            assertEquals("新的个人简介", result.getData().getBio());
            assertEquals("新学校", result.getData().getSchool());
            assertEquals("软件工程", result.getData().getMajor());
            assertEquals("大四", result.getData().getGrade());

            ArgumentCaptor<UserDetail> captor = ArgumentCaptor.forClass(UserDetail.class);
            verify(usersMapper).updateUserDetail(captor.capture());
            assertEquals("新昵称", captor.getValue().getNickname());
            assertEquals("13800138000", captor.getValue().getPhone());
            assertEquals("newemail@test.com", captor.getValue().getEmail());
            assertEquals("新的个人简介", captor.getValue().getBio());
            assertEquals("新学校", captor.getValue().getSchool());
            assertEquals("软件工程", captor.getValue().getMajor());
            assertEquals("大四", captor.getValue().getGrade());
        }

        @Test
        @DisplayName("should handle null DTO gracefully (no fields updated)")
        void updateProfileNullDto() {
            when(usersMapper.selectUserByUuid(userUuid)).thenReturn(user);
            when(usersMapper.selectUserDetailByUuid(userUuid)).thenReturn(userDetail);

            Result<UserProfileVO> result = userService.updateCurrentUserProfile(validToken, null);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            // Original values preserved
            assertEquals("昵称", result.getData().getNickname());
        }

        @Test
        @DisplayName("should create UserDetail when updating without existing detail")
        void updateProfileCreatesDetailWhenMissing() {
            when(usersMapper.selectUserByUuid(userUuid)).thenReturn(user);
            when(usersMapper.selectUserDetailByUuid(userUuid)).thenReturn(null);

            UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
            dto.setNickname("新昵称");

            Result<UserProfileVO> result = userService.updateCurrentUserProfile(validToken, dto);

            assertEquals(200, result.getCode());
            // Should have created a detail with default values + update values
            ArgumentCaptor<UserDetail> detailCaptor = ArgumentCaptor.forClass(UserDetail.class);
            verify(usersMapper).insertUserDetail(detailCaptor.capture());
            // Then should have updated it
            verify(usersMapper).updateUserDetail(any(UserDetail.class));
        }

        @Test
        @DisplayName("should return 401 when token is invalid")
        void updateProfileUnauthorized() {
            when(authService.getUserUuidByToken("bad-token")).thenReturn(null);

            UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
            dto.setNickname("新昵称");

            Result<UserProfileVO> result = userService.updateCurrentUserProfile("bad-token", dto);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
            verify(usersMapper, never()).updateUserDetail(any());
        }

        @Test
        @DisplayName("should return 401 when token is null")
        void updateProfileNullToken() {
            when(authService.getUserUuidByToken(null)).thenReturn(null);

            Result<UserProfileVO> result = userService.updateCurrentUserProfile(null, new UserProfileUpdateDTO());

            assertEquals(401, result.getCode());
            verify(usersMapper, never()).updateUserDetail(any());
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void updateProfileUserNotFound() {
            when(usersMapper.selectUserByUuid(userUuid)).thenReturn(null);

            Result<UserProfileVO> result = userService.updateCurrentUserProfile(validToken, new UserProfileUpdateDTO());

            assertEquals(404, result.getCode());
            assertEquals("用户不存在", result.getMessage());
            assertNull(result.getData());
            verify(usersMapper, never()).updateUserDetail(any());
        }

        @Test
        @DisplayName("should set updateTime when updating profile")
        void updateProfileSetsUpdateTime() {
            when(usersMapper.selectUserByUuid(userUuid)).thenReturn(user);
            when(usersMapper.selectUserDetailByUuid(userUuid)).thenReturn(userDetail);

            Result<UserProfileVO> result = userService.updateCurrentUserProfile(validToken, new UserProfileUpdateDTO());

            assertEquals(200, result.getCode());
            ArgumentCaptor<UserDetail> captor = ArgumentCaptor.forClass(UserDetail.class);
            verify(usersMapper).updateUserDetail(captor.capture());
            assertNotNull(captor.getValue().getUpdateTime());
        }
    }

    // ==================== uploadAvatar ====================

    @Nested
    @DisplayName("uploadAvatar Tests")
    class UploadAvatarTests {

        @Test
        @DisplayName("should upload avatar successfully")
        void uploadAvatarSuccess() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "avatar.jpg", "image/jpeg", "fake-image".getBytes());

            Result<UploadVO> result = userService.uploadAvatar(validToken, file);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().getUrl().startsWith("/api/uploads/avatars/mock-"));
            assertTrue(result.getData().getUrl().contains("avatar.jpg"));
        }

        @Test
        @DisplayName("should return 401 when token is invalid")
        void uploadAvatarUnauthorized() {
            when(authService.getUserUuidByToken("bad-token")).thenReturn(null);

            Result<UploadVO> result = userService.uploadAvatar("bad-token", null);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
        }

        @Test
        @DisplayName("should return 401 when token is null")
        void uploadAvatarNullToken() {
            when(authService.getUserUuidByToken(null)).thenReturn(null);

            Result<UploadVO> result = userService.uploadAvatar(null, null);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
        }

        @Test
        @DisplayName("should handle null file gracefully")
        void uploadAvatarNullFile() {
            Result<UploadVO> result = userService.uploadAvatar(validToken, null);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().getUrl().contains("avatar"));
            // should not contain the file extension since original filename is null
        }

        @Test
        @DisplayName("should handle file with null original filename")
        void uploadAvatarNullFilename() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", null, "image/jpeg", "fake-image".getBytes());

            Result<UploadVO> result = userService.uploadAvatar(validToken, file);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().getUrl().contains("avatar"));
        }

        @Test
        @DisplayName("should sanitize filename with path separators")
        void uploadAvatarSanitizesFilename() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "..\\evil.jpg", "image/jpeg", "fake-image".getBytes());

            Result<UploadVO> result = userService.uploadAvatar(validToken, file);

            assertEquals(200, result.getCode());
            // Path separators should be replaced with underscores
            assertTrue(result.getData().getUrl().contains("_evil.jpg")
                    || result.getData().getUrl().contains("_.."));
        }
    }
}
