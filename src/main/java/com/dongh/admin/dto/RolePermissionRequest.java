package com.dongh.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色分配权限请求
 */
@Data
public class RolePermissionRequest {

    private List<Long> permissionIds;
}
