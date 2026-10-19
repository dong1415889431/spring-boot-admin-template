package com.dongh.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色创建 / 更新请求
 */
@Data
public class RoleRequest {

    @NotBlank(message = "角色名不能为空")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    private String code;

    private String description;

    /** 状态：1 启用，0 禁用 */
    private Integer status;
}
