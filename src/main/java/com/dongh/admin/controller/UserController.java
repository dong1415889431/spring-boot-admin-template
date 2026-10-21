package com.dongh.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongh.admin.common.Result;
import com.dongh.admin.dto.UserRoleRequest;
import com.dongh.admin.dto.UserStatusRequest;
import com.dongh.admin.dto.UserUpdateRequest;
import com.dongh.admin.dto.UserVO;
import com.dongh.admin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User management endpoints (RBAC permission control)
 */
@Tag(name = "User Management", description = "Paginated user query, update, enable/disable, delete, and role assignment")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Paginated user query")
    @PreAuthorize("hasAuthority('user:list')")
    @GetMapping
    public Result<IPage<UserVO>> page(@RequestParam(defaultValue = "1") long page,
                                      @RequestParam(defaultValue = "10") long size,
                                      @RequestParam(required = false) String keyword) {
        return Result.success(userService.pageUsers(page, size, keyword));
    }

    @Operation(summary = "Update user")
    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success();
    }

    @Operation(summary = "Enable / disable user")
    @PreAuthorize("hasAuthority('user:status')")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusRequest request) {
        userService.updateStatus(id, request.getStatus());
        return Result.success();
    }

    @Operation(summary = "Delete user")
    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "Assign roles")
    @PreAuthorize("hasAuthority('user:assign-role')")
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody UserRoleRequest request) {
        userService.assignRoles(id, request.getRoleIds());
        return Result.success();
    }

    @Operation(summary = "Get user roles")
    @PreAuthorize("hasAuthority('user:list')")
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getRoles(@PathVariable Long id) {
        return Result.success(userService.getRoleIds(id));
    }
}
