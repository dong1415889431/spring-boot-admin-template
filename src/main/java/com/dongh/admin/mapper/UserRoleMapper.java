package com.dongh.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongh.admin.entity.UserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * User-role association mapper.
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * Query the role IDs assigned to a user.
     */
    @Select("SELECT ur.role_id FROM user_role ur WHERE ur.user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * Query the role names assigned to a user.
     */
    @Select("SELECT r.name FROM role r JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<String> selectRoleNamesByUserId(@Param("userId") Long userId);
}
