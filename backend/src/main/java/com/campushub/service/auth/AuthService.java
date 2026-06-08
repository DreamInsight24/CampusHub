package com.campushub.service.auth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.campushub.common.Result;
import com.campushub.dto.user.LoginDTO;
import com.campushub.dto.user.RegisterDTO;
import com.campushub.entity.User;
import com.campushub.entity.UserDetail;
import com.campushub.mapper.UsersMapper;
import com.campushub.util.AuthUtil;
import com.campushub.util.TokenUtil;
import com.campushub.vo.user.LoginVO;
import com.campushub.vo.user.UserVO;

@Service
public class AuthService {

    private static final int STATUS_ENABLED = 1;

    private final UsersMapper usersMapper;

    public AuthService(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    public Result<UserVO> register(RegisterDTO dto) {
        if (dto == null || isBlank(dto.getUsername()) || isBlank(dto.getPassword())) {
            return Result.error(400, "\u7528\u6237\u540d\u548c\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a");
        }

        User existing = usersMapper.selectUserByUsername(dto.getUsername());
        if (existing != null) {
            return Result.error(409, "\u7528\u6237\u540d\u5df2\u5b58\u5728");
        }

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername(dto.getUsername());
        user.setPasswordHash(AuthUtil.hashPassword(dto.getPassword()));
        user.setStatus(STATUS_ENABLED);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        usersMapper.insertUser(user);

        UserDetail detail = new UserDetail();
        detail.setUserUuid(user.getUuid());
        detail.setNickname(user.getUsername());
        detail.setCreditScore(100);
        detail.setInterests(List.of());
        detail.setTags(List.of());
        detail.setCreateTime(user.getCreateTime());
        detail.setUpdateTime(user.getUpdateTime());
        usersMapper.insertUserDetail(detail);

        UserVO vo = new UserVO();
        vo.setUserUuid(user.getUuid());
        vo.setUsername(user.getUsername());

        return Result.success(vo);
    }

    public Result<LoginVO> login(LoginDTO dto) {
        if (dto == null || isBlank(dto.getUsername()) || isBlank(dto.getPassword())) {
            return Result.error(400, "\u8d26\u53f7\u548c\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a");
        }

        User user = usersMapper.selectUserByUsername(dto.getUsername());
        if (user == null) {
            return Result.error(401, "\u8d26\u53f7\u6216\u5bc6\u7801\u9519\u8bef");
        }

        if (user.getStatus() == null || user.getStatus() != STATUS_ENABLED) {
            return Result.error(403, "\u8d26\u53f7\u5df2\u88ab\u7981\u7528");
        }

        if (!AuthUtil.verifyPassword(dto.getPassword(), user.getPasswordHash())) {
            return Result.error(401, "\u8d26\u53f7\u6216\u5bc6\u7801\u9519\u8bef");
        }

        String token = TokenUtil.generateToken(user.getUuid());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserUuid(user.getUuid());

        return Result.success(loginVO);
    }

    public UUID getUserUuidByToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String normalizedToken = token;
        if (normalizedToken.startsWith("Bearer ")) {
            normalizedToken = normalizedToken.substring("Bearer ".length());
        }

        return TokenUtil.parseToken(normalizedToken);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
