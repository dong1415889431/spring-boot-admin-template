package com.dongh.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongh.admin.entity.Permission;

import java.util.List;

/**
 * 权限服务
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 查询全部权限（按排序号升序）
     */
    List<Permission> listAll();
}
