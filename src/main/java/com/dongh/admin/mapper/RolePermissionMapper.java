package com.dongh.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongh.admin.entity.RolePermission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Role-permission association mapper.
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * Query the permission IDs assigned to a role.
     */
    @Select("SELECT rp.permission_id FROM role_permission rp WHERE rp.role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
