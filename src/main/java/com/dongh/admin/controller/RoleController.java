package com.dongh.admin.controller;

import com.dongh.admin.common.Result;
import com.dongh.admin.dto.RolePermissionRequest;
import com.dongh.admin.dto.RoleRequest;
import com.dongh.admin.entity.Role;
import com.dongh.admin.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Role management endpoints (RBAC permission control)
 */
@Tag(name = "Role Management", description = "Role CRUD and permission assignment")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Role list")
    @PreAuthorize("hasAuthority('role:list')")
    @GetMapping
    public Result<List<Role>> list() {
        return Result.success(roleService.listAll());
    }

    @Operation(summary = "Create role")
    @PreAuthorize("hasAuthority('role:create')")
    @PostMapping
    public Result<Role> create(@Valid @RequestBody RoleRequest request) {
        return Result.success(roleService.createRole(request));
    }

    @Operation(summary = "Update role")
    @PreAuthorize("hasAuthority('role:update')")
    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return Result.success(roleService.updateRole(id, request));
    }

    @Operation(summary = "Delete role")
    @PreAuthorize("hasAuthority('role:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "Assign permissions")
    @PreAuthorize("hasAuthority('role:assign-permission')")
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody RolePermissionRequest request) {
        roleService.assignPermissions(id, request.getPermissionIds());
        return Result.success();
    }

    @Operation(summary = "Get role permissions")
    @PreAuthorize("hasAuthority('role:list')")
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable Long id) {
        return Result.success(roleService.getPermissionIds(id));
    }
}
