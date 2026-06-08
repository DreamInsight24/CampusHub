package com.campushub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.campushub.common.Result;
import com.campushub.dto.user.LoginDTO;
import com.campushub.dto.user.RegisterDTO;
import com.campushub.service.auth.AuthService;
import com.campushub.vo.user.LoginVO;
import com.campushub.vo.user.UserVO;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void registerShouldCallServiceAndReturnUserVO() throws Exception {
        UserVO userVO = new UserVO();
        userVO.setUserUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        userVO.setUsername("alice");

        when(authService.register(any(RegisterDTO.class))).thenReturn(Result.success(userVO));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.userUuid").value("11111111-1111-1111-1111-111111111111"));

        verify(authService).register(any(RegisterDTO.class));
    }

    @Test
    void loginShouldCallServiceAndReturnLoginVO() throws Exception {
        LoginVO loginVO = new LoginVO();
        loginVO.setToken("mock-token");
        loginVO.setUserUuid(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        when(authService.login(any(LoginDTO.class))).thenReturn(Result.success(loginVO));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("mock-token"))
                .andExpect(jsonPath("$.data.userUuid").value("22222222-2222-2222-2222-222222222222"));

        verify(authService).login(any(LoginDTO.class));
    }

    @Test
    void registerShouldReturn400WhenServiceReturnsValidationError() throws Exception {
        when(authService.register(any(RegisterDTO.class)))
                .thenReturn(Result.error(400, "用户名和密码不能为空"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名和密码不能为空"));

        verify(authService).register(any(RegisterDTO.class));
    }

    @Test
    void registerShouldReturn409WhenUsernameDuplicate() throws Exception {
        when(authService.register(any(RegisterDTO.class)))
                .thenReturn(Result.error(409, "用户名已存在"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        verify(authService).register(any(RegisterDTO.class));
    }

    @Test
    void loginShouldReturn400WhenServiceReturnsValidationError() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenReturn(Result.error(400, "账号和密码不能为空"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("账号和密码不能为空"));

        verify(authService).login(any(LoginDTO.class));
    }

    @Test
    void loginShouldReturn401WhenCredentialsInvalid() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenReturn(Result.error(401, "账号或密码错误"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"wrong\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("账号或密码错误"));

        verify(authService).login(any(LoginDTO.class));
    }

    @Test
    void loginShouldReturn403WhenUserDisabled() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenReturn(Result.error(403, "账号已被禁用"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("账号已被禁用"));

        verify(authService).login(any(LoginDTO.class));
    }
}
