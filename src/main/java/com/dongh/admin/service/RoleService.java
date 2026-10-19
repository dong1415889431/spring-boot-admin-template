package com.dongh.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongh.admin.dto.RoleRequest;
import com.dongh.admin.entity.Role;

import java.util.List;

/**
 * 角色服务
 */
public interface RoleService extends IService<Role> {

    List<Role> listAll();

    Role createRole(RoleRequest request);

    Role updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    List<Long> getPermissionIds(Long roleId);
}
