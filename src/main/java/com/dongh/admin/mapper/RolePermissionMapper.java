package com.dongh.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongh.admin.entity.RolePermission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色-权限关联 Mapper
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 查询角色拥有的权限 ID 列表
     */
    @Select("SELECT rp.permission_id FROM role_permission rp WHERE rp.role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
