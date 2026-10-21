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
 * User service
 */
public interface UserService extends IService<User> {

    User getByUsername(String username);

    boolean existsByUsername(String username);

    User register(RegisterRequest request, PasswordEncoder passwordEncoder);

    /** Paginated user query (keyword fuzzy-matches username/nickname/email) */
    IPage<UserVO> pageUsers(long page, long size, String keyword);

    void updateUser(Long id, UserUpdateRequest request);

    void updateStatus(Long id, Integer status);

    void deleteUser(Long id);

    /** Reassign user roles (full overwrite) */
    void assignRoles(Long userId, List<Long> roleIds);

    List<Long> getRoleIds(Long userId);
}
