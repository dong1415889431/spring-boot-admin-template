package com.dongh.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongh.admin.entity.Permission;
import com.dongh.admin.mapper.PermissionMapper;
import com.dongh.admin.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务实现
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public List<Permission> listAll() {
        return lambdaQuery().orderByAsc(Permission::getSort).list();
    }
}
