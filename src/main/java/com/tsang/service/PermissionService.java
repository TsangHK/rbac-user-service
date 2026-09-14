package com.tsang.service;

import java.util.List;

/**
 * 权限业务接口
 */
public interface PermissionService {

    /**
     * 根据用户ID查询权限编码
     *
     * @param userId 用户ID
     * @return 权限编码集合
     */
    List<String> getPermissionCodes(Integer userId);
}