package com.tsang.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tsang.entity.Permission;
import com.tsang.entity.RolePermission;
import com.tsang.entity.UserRole;
import com.tsang.mapper.PermissionMapper;
import com.tsang.mapper.RolePermissionMapper;
import com.tsang.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 权限业务实现
 */
@Service
public class PermissionServiceImpl
        implements PermissionService {


    // 用户角色关系表
    @Resource
    private UserRoleMapper userRoleMapper;


    // 角色权限关系表
    @Resource
    private RolePermissionMapper rolePermissionMapper;


    // 权限表
    @Resource
    private PermissionMapper permissionMapper;


    /**
     * 查询用户权限编码
     */
    @Override
    public List<String> getPermissionCodes(Integer userId) {


        // 第一步：查询用户的所有角色
        List<UserRole> userRoles =
                userRoleMapper.selectList(
                        new LambdaQueryWrapper<UserRole>()
                                .eq(UserRole::getUserId, userId)
                );


        // 用户没有角色
        if (userRoles.isEmpty()) {

            return List.of();

        }


        List<Integer> roleIds =
                userRoles.stream()
                        .map(UserRole::getRoleId)
                        .toList();


        // 第二步：查询这些角色关联的所有权限ID（去重）
        List<Integer> permissionIds =
                rolePermissionMapper.selectList(
                                new LambdaQueryWrapper<RolePermission>()
                                        .in(RolePermission::getRoleId, roleIds)
                        )
                        .stream()
                        .map(RolePermission::getPermissionId)
                        .distinct()
                        .toList();


        // 角色没有配置任何权限
        if (permissionIds.isEmpty()) {

            return List.of();

        }


        // 第三步：批量查询权限，取出编码
        return permissionMapper.selectByIds(permissionIds)
                .stream()
                .map(Permission::getCode)
                .toList();


    }


}
