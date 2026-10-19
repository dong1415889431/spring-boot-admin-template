package com.dongh.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户启禁用请求
 */
@Data
public class UserStatusRequest {

    @NotNull
    @Min(0)
    @Max(1)
    private Integer status;
}
