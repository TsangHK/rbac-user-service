package com.tsang.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsang.annotation.RequiresPermission;
import com.tsang.common.Result;
import com.tsang.dto.UserUpdateDTO;
import com.tsang.entity.User;
import com.tsang.exception.BusinessException;
import com.tsang.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理接口，通过 @RequiresPermission 声明所需权限编码
 */
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询全部用户
     */
    @RequiresPermission("user:list")
    @GetMapping("/findAll")
    public Result findAll() {
        List<User> list = userService.findAll();
        return Result.success(list);
    }

    /**
     * 分页 + 条件查询，name、age可不传，不传则不加该条件
     */
    @RequiresPermission("user:list")
    @GetMapping("/page")
    public Result page(
            @RequestParam @Min(value = 1, message = "页码必须大于0") Integer page,
            @RequestParam
            @Min(value = 1, message = "每页条数必须大于0")
            @Max(value = 100, message = "每页条数不能超过100") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age) {

        Page<User> pageInfo = userService.page(page, size, name, age);

        return Result.success(pageInfo);
    }

    /**
     * 根据ID查询用户，用户不存在时抛业务异常
     */
    @RequiresPermission("user:list")
    @GetMapping("/find")
    public Result find(@RequestParam Integer id) {

        User user = userService.findById(id);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return Result.success(user);
    }

    /**
     * 新增用户，@Valid 触发实体上的校验注解
     */
    @RequiresPermission("user:add")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody User user) {

        userService.add(user);

        return Result.success(null);
    }

    /**
     * 修改用户，入参 UserUpdateDTO 不含密码字段
     */
    @RequiresPermission("user:update")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UserUpdateDTO user) {

        // DTO转实体：只带允许修改的字段
        User entity = new User();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setAge(user.getAge());
        entity.setUsername(user.getUsername());

        userService.update(entity);

        return Result.success(null);
    }

    /**
     * 删除用户
     */
    @RequiresPermission("user:delete")
    @DeleteMapping("/delete")
    public Result delete(@RequestParam Integer id) {

        userService.delete(id);

        return Result.success(null);
    }
}
