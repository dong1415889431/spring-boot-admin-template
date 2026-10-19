package com.dongh.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongh.admin.entity.Permission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 查询用户拥有的全部权限编码（通过 用户→角色→权限 关联）
     */
    @Select("SELECT DISTINCT p.code FROM permission p " +
            "JOIN role_permission rp ON p.id = rp.permission_id " +
            "JOIN user_role ur ON rp.role_id = ur.role_id " +
            "JOIN role r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} " +
            "AND p.deleted = 0 AND r.deleted = 0 AND r.status = 1")
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
