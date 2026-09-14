package com.tsang.controller;

import com.tsang.common.Result;
import com.tsang.dto.LoginDTO;
import com.tsang.entity.User;
import com.tsang.exception.UnauthorizedException;
import com.tsang.service.PermissionService;
import com.tsang.service.UserService;
import com.tsang.utils.JwtUtils;
import com.tsang.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录接口：校验账号密码并签发携带权限的Token
 */
@RestController
public class LoginController {

    // 用户业务
    private final UserService userService;

    // 权限业务
    private final PermissionService permissionService;

    // JWT工具
    private final JwtUtils jwtUtils;

    public LoginController(UserService userService, PermissionService permissionService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 登录：校验账号密码并签发Token
     */
    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDTO loginDTO) {
        // 验证账号密码
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

        // 用户不存在或密码错误，抛认证异常走真实 HTTP 401
        if (user == null) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        // 查询用户权限编码
        List<String> permissions = permissionService.getPermissionCodes(user.getId());

        // 生成JWT
        String token = jwtUtils.createToken(user.getId(), user.getUsername(), permissions);

        // 封装用户信息（不含密码）
        LoginVO.UserInfo userInfo = new LoginVO.UserInfo(user.getId(), user.getName(), user.getUsername());

        // 组装返回：Token + 用户信息 + 权限列表
        LoginVO loginVO = new LoginVO(token, userInfo, permissions);

        return Result.success(loginVO);
    }
}
