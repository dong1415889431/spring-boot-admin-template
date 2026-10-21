package com.dongh.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * User role assignment request.
 */
@Data
public class UserRoleRequest {

    private List<Long> roleIds;
}
