package com.dongh.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * Role permission assignment request.
 */
@Data
public class RolePermissionRequest {

    private List<Long> permissionIds;
}
