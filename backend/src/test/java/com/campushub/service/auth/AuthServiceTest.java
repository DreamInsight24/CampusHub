package com.campushub.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.campushub.common.Result;
import com.campushub.dto.user.LoginDTO;
import com.campushub.dto.user.RegisterDTO;
import com.campushub.entity.User;
import com.campushub.mapper.UsersMapper;
import com.campushub.util.AuthUtil;
import com.campushub.vo.user.LoginVO;
import com.campushub.vo.user.UserVO;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UsersMapper usersMapper;

    @InjectMocks
    private AuthService authService;

    private RegisterDTO validRegisterDTO;
    private LoginDTO validLoginDTO;
    private User savedUser;

    @BeforeEach
    void setUp() {
        validRegisterDTO = new RegisterDTO();
        validRegisterDTO.setUsername("testuser");
        validRegisterDTO.setPassword("password123");

        validLoginDTO = new LoginDTO();
        validLoginDTO.setUsername("testuser");
        validLoginDTO.setPassword("password123");

        savedUser = new User();
        savedUser.setUuid(UUID.randomUUID());
        savedUser.setUsername("testuser");
        savedUser.setPasswordHash(AuthUtil.hashPassword("password123"));
        savedUser.setStatus(1);
        savedUser.setCreateTime(LocalDateTime.now());
        savedUser.setUpdateTime(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("should register successfully with valid input")
        void registerSuccess() {
            when(usersMapper.selectUserByUsername("testuser")).thenReturn(null);
            when(usersMapper.insertUser(any(User.class))).thenReturn(1);

            Result<UserVO> result = authService.register(validRegisterDTO);

            assertEquals(200, result.getCode());
            assertEquals("success", result.getMessage());
            assertNotNull(result.getData());
            assertEquals("testuser", result.getData().getUsername());
            assertNotNull(result.getData().getUserUuid());

            verify(usersMapper).selectUserByUsername("testuser");
            verify(usersMapper).insertUser(any(User.class));
        }

        @Test
        @DisplayName("should return 400 when username is empty")
        void registerEmptyUsername() {
            validRegisterDTO.setUsername("");

            Result<UserVO> result = authService.register(validRegisterDTO);

            assertEquals(400, result.getCode());
            assertNull(result.getData());

            verify(usersMapper, never()).insertUser(any(User.class));
        }

        @Test
        @DisplayName("should return 400 when password is empty")
        void registerEmptyPassword() {
            validRegisterDTO.setPassword("");

            Result<UserVO> result = authService.register(validRegisterDTO);

            assertEquals(400, result.getCode());
            assertNull(result.getData());

            verify(usersMapper, never()).insertUser(any(User.class));
        }

        @Test
        @DisplayName("should return 400 when DTO is null")
        void registerNullDto() {
            Result<UserVO> result = authService.register(null);

            assertEquals(400, result.getCode());
            assertNull(result.getData());

            verify(usersMapper, never()).insertUser(any(User.class));
        }

        @Test
        @DisplayName("should return 400 when username is blank (whitespace only)")
        void registerBlankUsername() {
            validRegisterDTO.setUsername("   ");

            Result<UserVO> result = authService.register(validRegisterDTO);

            assertEquals(400, result.getCode());
            assertNull(result.getData());

            verify(usersMapper, never()).insertUser(any(User.class));
        }

        @Test
        @DisplayName("should return 409 when username already exists")
        void registerDuplicateUsername() {
            when(usersMapper.selectUserByUsername("testuser")).thenReturn(savedUser);

            Result<UserVO> result = authService.register(validRegisterDTO);

            assertEquals(409, result.getCode());
            assertEquals("用户名已存在", result.getMessage());
            assertNull(result.getData());

            verify(usersMapper).selectUserByUsername("testuser");
            verify(usersMapper, never()).insertUser(any(User.class));
        }

        @Test
        @DisplayName("should hash password and not store plaintext")
        void registerPasswordIsHashed() {
            when(usersMapper.selectUserByUsername("testuser")).thenReturn(null);
            when(usersMapper.insertUser(any(User.class))).thenReturn(1);

            authService.register(validRegisterDTO);

            verify(usersMapper).insertUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("should login successfully with valid credentials")
        void loginSuccess() {
            when(usersMapper.selectUserByUsername("testuser")).thenReturn(savedUser);

            Result<LoginVO> result = authService.login(validLoginDTO);

            assertEquals(200, result.getCode());
            assertEquals("success", result.getMessage());
            assertNotNull(result.getData());
            assertNotNull(result.getData().getToken());
            assertTrue(result.getData().getToken().length() > 0);
            assertEquals(savedUser.getUuid(), result.getData().getUserUuid());

            verify(usersMapper).selectUserByUsername("testuser");
        }

        @Test
        @DisplayName("should return 401 when user does not exist")
        void loginUserNotFound() {
            when(usersMapper.selectUserByUsername("testuser")).thenReturn(null);

            Result<LoginVO> result = authService.login(validLoginDTO);

            assertEquals(401, result.getCode());
            assertEquals("账号或密码错误", result.getMessage());
            assertNull(result.getData());

            verify(usersMapper).selectUserByUsername("testuser");
        }

        @Test
        @DisplayName("should return 401 when password is wrong")
        void loginWrongPassword() {
            validLoginDTO.setPassword("wrongpassword");
            when(usersMapper.selectUserByUsername("testuser")).thenReturn(savedUser);

            Result<LoginVO> result = authService.login(validLoginDTO);

            assertEquals(401, result.getCode());
            assertEquals("账号或密码错误", result.getMessage());
            assertNull(result.getData());

            verify(usersMapper).selectUserByUsername("testuser");
        }

        @Test
        @DisplayName("should return 403 when user status is disabled (0)")
        void loginUserDisabled() {
            savedUser.setStatus(0);
            when(usersMapper.selectUserByUsername("testuser")).thenReturn(savedUser);

            Result<LoginVO> result = authService.login(validLoginDTO);

            assertEquals(403, result.getCode());
            assertEquals("账号已被禁用", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        @DisplayName("should return 403 when user status is null")
        void loginUserStatusNull() {
            savedUser.setStatus(null);
            when(usersMapper.selectUserByUsername("testuser")).thenReturn(savedUser);

            Result<LoginVO> result = authService.login(validLoginDTO);

            assertEquals(403, result.getCode());
            assertEquals("账号已被禁用", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        @DisplayName("should return 400 when username is empty")
        void loginEmptyUsername() {
            validLoginDTO.setUsername("");

            Result<LoginVO> result = authService.login(validLoginDTO);

            assertEquals(400, result.getCode());
            assertNull(result.getData());

            verify(usersMapper, never()).selectUserByUsername(any(String.class));
        }

        @Test
        @DisplayName("should return 400 when password is empty")
        void loginEmptyPassword() {
            validLoginDTO.setPassword("");

            Result<LoginVO> result = authService.login(validLoginDTO);

            assertEquals(400, result.getCode());
            assertNull(result.getData());

            verify(usersMapper, never()).selectUserByUsername(any(String.class));
        }

        @Test
        @DisplayName("should return 400 when DTO is null")
        void loginNullDto() {
            Result<LoginVO> result = authService.login(null);

            assertEquals(400, result.getCode());
            assertNull(result.getData());

            verify(usersMapper, never()).selectUserByUsername(any(String.class));
        }
    }
}
