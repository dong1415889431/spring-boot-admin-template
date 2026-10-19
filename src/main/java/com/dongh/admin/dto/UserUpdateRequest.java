package com.dongh.admin.dto;

import lombok.Data;

/**
 * 用户更新请求（昵称 / 邮箱）
 */
@Data
public class UserUpdateRequest {

    private String nickname;

    private String email;
}
