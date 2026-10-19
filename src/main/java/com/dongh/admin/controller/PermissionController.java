package com.dongh.admin.controller;

import com.dongh.admin.common.Result;
import com.dongh.admin.entity.Permission;
import com.dongh.admin.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限管理接口（RBAC 权限控制）
 */
@Tag(name = "权限管理", description = "权限列表查询")
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(summary = "权限列表")
    @PreAuthorize("hasAuthority('permission:list')")
    @GetMapping
    public Result<List<Permission>> list() {
        return Result.success(permissionService.listAll());
    }
}
