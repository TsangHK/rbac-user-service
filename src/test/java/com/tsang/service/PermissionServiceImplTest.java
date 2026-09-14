package com.tsang.service;

import com.tsang.entity.Permission;
import com.tsang.entity.RolePermission;
import com.tsang.entity.UserRole;
import com.tsang.mapper.PermissionMapper;
import com.tsang.mapper.RolePermissionMapper;
import com.tsang.mapper.UserRoleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 权限业务测试（Mock数据库层）
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Test
    void 多角色的权限合并返回() {

        // 用户同时拥有角色100和角色101
        when(userRoleMapper.selectList(any()))
                .thenReturn(List.of(
                        userRole(1, 100),
                        userRole(1, 101)
                ));

        // 角色100：权限200、201；角色101：权限202
        when(rolePermissionMapper.selectList(any()))
                .thenReturn(List.of(
                        rolePermission(100, 200),
                        rolePermission(100, 201),
                        rolePermission(101, 202)
                ));

        when(permissionMapper.selectByIds(anyCollection()))
                .thenReturn(List.of(
                        permission(200, "user:list"),
                        permission(201, "user:add"),
                        permission(202, "user:update")
                ));

        List<String> codes = permissionService.getPermissionCodes(1);

        assertThat(codes).containsExactlyInAnyOrder(
                "user:list", "user:add", "user:update");
    }

    @Test
    void 用户没有角色时返回空() {

        when(userRoleMapper.selectList(any()))
                .thenReturn(List.of());

        assertThat(permissionService.getPermissionCodes(99)).isEmpty();

        // 不应再查询角色权限表、权限表
        verifyNoInteractions(rolePermissionMapper, permissionMapper);
    }

    private UserRole userRole(Integer userId, Integer roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        return userRole;
    }

    private RolePermission rolePermission(Integer roleId, Integer permissionId) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionId(permissionId);
        return rolePermission;
    }

    private Permission permission(Integer id, String code) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setCode(code);
        return permission;
    }
}
