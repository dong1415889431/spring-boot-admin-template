package com.dongh.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户分配角色请求
 */
@Data
public class UserRoleRequest {

    private List<Long> roleIds;
}
