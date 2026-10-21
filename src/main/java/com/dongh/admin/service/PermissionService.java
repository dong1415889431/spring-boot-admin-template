package com.dongh.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongh.admin.entity.Permission;

import java.util.List;

/**
 * Permission service
 */
public interface PermissionService extends IService<Permission> {

    /**
     * Query all permissions (ascending by sort order)
     */
    List<Permission> listAll();
}
