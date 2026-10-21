package com.dongh.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongh.admin.common.ResultCode;
import com.dongh.admin.common.exception.BusinessException;
import com.dongh.admin.dto.RegisterRequest;
import com.dongh.admin.dto.UserUpdateRequest;
import com.dongh.admin.dto.UserVO;
import com.dongh.admin.entity.User;
import com.dongh.admin.entity.UserRole;
import com.dongh.admin.mapper.UserMapper;
import com.dongh.admin.mapper.UserRoleMapper;
import com.dongh.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User service implementation
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;

    @Override
    public User getByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).one();
    }

    @Override
    public boolean existsByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).exists();
    }

    @Override
    public User register(RegisterRequest request, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        save(user);
        return user;
    }

    @Override
    public IPage<UserVO> pageUsers(long page, long size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getEmail, keyword));
        }
        wrapper.orderByDesc(User::getCreatedAt);
        IPage<User> userPage = page(new Page<>(page, size), wrapper);

        List<UserVO> records = userPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        Page<UserVO> result = new Page<>(page, size, userPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    public void updateUser(Long id, UserUpdateRequest request) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "User not found");
        }
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "User not found");
        }
        user.setStatus(status);
        updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "User not found");
        }
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id));
        removeById(id);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        if (getById(userId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "User not found");
        }
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setRoleIds(userRoleMapper.selectRoleIdsByUserId(user.getId()));
        vo.setRoleNames(userRoleMapper.selectRoleNamesByUserId(user.getId()));
        return vo;
    }
}
