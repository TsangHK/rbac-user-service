package com.tsang.controller;


import com.tsang.common.Result;
import com.tsang.entity.User;
import com.tsang.exception.BusinessException;
import com.tsang.service.UserService;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;


/**
 * 当前用户信息接口：登录后的用户查看自己的资料
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {


    private final UserService userService;


    public UserInfoController(UserService userService) {

        this.userService = userService;

    }


    /**
     * 获取当前登录用户信息，用户ID取自拦截器放入request的属性
     */
    @GetMapping("/info")
    public Result info(HttpServletRequest request) {


        // 从JWT拦截器放入的属性中取当前用户ID
        Integer userId =
                (Integer) request.getAttribute("userId");


        // 根据ID查询最新的用户信息
        User user =
                userService.findById(userId);


        // 用户不存在时抛出业务异常
        if (user == null) {

            throw new BusinessException("用户不存在或已被删除");

        }


        return Result.success(user);

    }


}
