package com.dongh.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dongh.admin.dto.RegisterRequest;
import com.dongh.admin.dto.UserUpdateRequest;
import com.dongh.admin.dto.UserVO;
import com.dongh.admin.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    User getByUsername(String username);

    boolean existsByUsername(String username);

    User register(RegisterRequest request, PasswordEncoder passwordEncoder);

    /** 分页查询用户（关键字模糊匹配用户名/昵称/邮箱） */
    IPage<UserVO> pageUsers(long page, long size, String keyword);

    void updateUser(Long id, UserUpdateRequest request);

    void updateStatus(Long id, Integer status);

    void deleteUser(Long id);

    /** 重新分配用户角色（覆盖式） */
    void assignRoles(Long userId, List<Long> roleIds);

    List<Long> getRoleIds(Long userId);
}
