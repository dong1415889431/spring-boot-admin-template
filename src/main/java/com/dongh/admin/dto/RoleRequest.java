package com.dongh.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Role create / update request.
 */
@Data
public class RoleRequest {

    @NotBlank(message = "Role name is required")
    private String name;

    @NotBlank(message = "Role code is required")
    private String code;

    private String description;

    /** Status: 1 enabled, 0 disabled. */
    private Integer status;
}
