package com.dongh.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * 说明：第 3 周只做认证，先建 user 表；role/permission 相关表第 4 周（RBAC）再补充。
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** 密码（BCrypt 加密），序列化时忽略，避免泄露 */
    @JsonIgnore
    private String password;

    private String nickname;

    private String email;

    /** 状态：1 启用，0 禁用 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** 逻辑删除：0 未删除，1 已删除 */
    @TableLogic
    private Integer deleted;
}
