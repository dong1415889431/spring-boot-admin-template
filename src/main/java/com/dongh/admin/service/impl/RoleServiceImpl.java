package com.dongh.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongh.admin.common.ResultCode;
import com.dongh.admin.common.exception.BusinessException;
import com.dongh.admin.dto.RoleRequest;
import com.dongh.admin.entity.Role;
import com.dongh.admin.entity.RolePermission;
import com.dongh.admin.entity.UserRole;
import com.dongh.admin.mapper.RoleMapper;
import com.dongh.admin.mapper.RolePermissionMapper;
import com.dongh.admin.mapper.UserRoleMapper;
import com.dongh.admin.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public List<Role> listAll() {
        return lambdaQuery().orderByAsc(Role::getId).list();
    }

    @Override
    public Role createRole(RoleRequest request) {
        if (lambdaQuery().eq(Role::getCode, request.getCode()).exists()) {
            throw new BusinessException(ResultCode.CONFLICT, "角色编码已存在");
        }
        Role role = new Role();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        save(role);
        return role;
    }

    @Override
    public Role updateRole(Long id, RoleRequest request) {
        Role role = getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        boolean codeTaken = lambdaQuery()
                .eq(Role::getCode, request.getCode())
                .ne(Role::getId, id)
                .exists();
        if (codeTaken) {
            throw new BusinessException(ResultCode.CONFLICT, "角色编码已存在");
        }
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        updateById(role);
        return role;
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        // 清理关联数据，再逻辑删除角色
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id));
        removeById(id);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        if (getById(roleId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        if (permissionIds != null) {
            for (Long permissionId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    public List<Long> getPermissionIds(Long roleId) {
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }
}
