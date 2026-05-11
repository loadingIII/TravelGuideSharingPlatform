package com.travel.service.impl;

import com.travel.pojo.dto.AuthLoginDTO;
import com.travel.pojo.dto.AuthRegisterDTO;
import com.travel.pojo.vo.AuthTokenVO;
import com.travel.pojo.vo.UserMeVO;
import com.travel.security.JwtTokenService;
import com.travel.security.LoginUser;
import com.travel.pojo.common.exception.BusinessException;
import com.travel.pojo.common.exception.ErrorCode;

import com.travel.pojo.model.User;
import com.travel.pojo.model.UserProfile;
import com.travel.mapper.UserMapper;
import com.travel.mapper.UserProfileMapper;
import com.travel.service.AuthService;
import com.travel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 认证服务实现类
 * 负责用户注册、登录、令牌刷新等认证流程
 *
 * 核心流程：
 * 1. 注册：校验用户名/手机号/邮箱唯一性 → 创建用户 → 创建用户资料 → 签发令牌
 * 2. 登录：通过手机号查找用户 → 校验密码 → 更新登录时间 → 签发令牌
 * 3. 刷新：解析旧令牌 → 重新签发新令牌（不验证密码）
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    /**
     * 用户注册
     * 整个过程在一个事务中完成，保证用户和用户资料同时创建成功或同时回滚
     */
    @Override
    @Transactional
    public AuthTokenVO register(AuthRegisterDTO request) {
        // 1. 校验注册信息的唯一性（用户名、手机号、邮箱不能重复）
        validateRegisterRequest(request);

        // 2. 创建用户基础账号，密码使用 BCrypt 加密存储
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPhone(emptyToNull(request.getPhone()));
        user.setEmail(emptyToNull(request.getEmail()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(1); // 状态1表示账号正常启用
        userMapper.insert(user); // 插入后 MyBatis 会自动回填 user.getId()

        // 3. 创建用户资料（昵称默认使用用户名，头像和个人简介为空）
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        profile.setAvatarUrl(null);
        profile.setBio(null);
        profile.setIsVip(false);
        userProfileMapper.insert(profile);

        // 4. 构建完整的用户信息并签发 JWT 令牌返回
        UserMeVO userMe = userService.getByUserId(user.getId());
        return buildTokenResponse(user, userMe);
    }

    /**
     * 用户登录
     * 通过手机号 + 密码进行身份验证，验证成功后更新最后登录时间
     */
    @Override
    public AuthTokenVO login(AuthLoginDTO request) {
        // 1. 根据手机号查找用户
        User user = userMapper.selectByPhone(request.getPhone());

        // 2. 校验用户是否存在以及密码是否正确（BCrypt 比对）
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "手机号或密码错误");
        }

        // 3. 校验账号状态是否正常（status=1 表示正常，其他值表示禁用/冻结等）
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号不可用");
        }

        // 4. 更新最后登录时间（用于记录用户活跃情况）
        userMapper.updateLastLoginAt(user.getId());

        // 5. 签发新的 JWT 令牌并返回
        UserMeVO userMe = userService.getByUserId(user.getId());
        return buildTokenResponse(user, userMe);
    }

    /**
     * 刷新访问令牌
     * 客户端可以用旧的（可能即将过期的）令牌换取一个新的令牌
     * 注意：这里只是重新签发令牌，不验证密码，安全性依赖旧令牌本身的有效性
     */
    @Override
    public AuthTokenVO refresh(String authorization) {
        // 1. 从请求头中提取 Bearer token
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的登录凭证");
        }
        String token = authorization.substring("Bearer ".length()).trim();

        // 2. 解析旧令牌，获取用户信息
        LoginUser loginUser = jwtTokenService.parseAccessToken(token);

        // 3. 校验用户是否仍然存在
        User user = userMapper.selectById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 4. 签发全新的令牌返回
        UserMeVO userMe = userService.getByUserId(user.getId());
        return buildTokenResponse(user, userMe);
    }

    /**
     * 构建令牌响应
     * 将用户信息和新签发的 JWT 令牌打包成统一的响应格式
     */
    private AuthTokenVO buildTokenResponse(User user, UserMeVO userMe) {
        String token = jwtTokenService.generateAccessToken(user.getId(), user.getUsername());
        long expiresInSeconds = jwtTokenService.getAccessExpireMinutes() * 60;
        return new AuthTokenVO(token, "Bearer", expiresInSeconds, userMe);
    }

    /**
     * 校验注册请求的唯一性
     * 检查用户名、手机号、邮箱是否已被其他用户占用
     */
    private void validateRegisterRequest(AuthRegisterDTO request) {
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "用户名已存在");
        }
        if (StringUtils.hasText(request.getPhone()) && userMapper.selectByPhone(request.getPhone()) != null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "手机号已存在");
        }
        if (StringUtils.hasText(request.getEmail()) && userMapper.selectByEmail(request.getEmail()) != null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "邮箱已存在");
        }
    }

    /**
     * 空字符串转 null
     * 避免将空字符串存入数据库，保持数据整洁
     */
    private String emptyToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
