package com.dongh.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户列表视图对象（含角色信息）
 */
@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    /** 状态：1 启用，0 禁用 */
    private Integer status;

    private LocalDateTime createdAt;

    /** 拥有的角色 ID 列表 */
    private List<Long> roleIds;

    /** 拥有的角色名列表 */
    private List<String> roleNames;
}
