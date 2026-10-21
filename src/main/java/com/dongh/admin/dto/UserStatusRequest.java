package com.dongh.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * User enable/disable request.
 */
@Data
public class UserStatusRequest {

    @NotNull
    @Min(0)
    @Max(1)
    private Integer status;
}
