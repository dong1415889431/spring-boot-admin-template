package com.dongh.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongh.admin.entity.UserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户-角色关联 Mapper
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 查询用户拥有的角色 ID 列表
     */
    @Select("SELECT ur.role_id FROM user_role ur WHERE ur.user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户拥有的角色名列表
     */
    @Select("SELECT r.name FROM role r JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<String> selectRoleNamesByUserId(@Param("userId") Long userId);
}
