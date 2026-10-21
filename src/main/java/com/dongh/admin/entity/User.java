package com.dongh.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * User entity.
 *
 * Note: Week 3 only covers authentication, so only the user table is created;
 * the role/permission tables are added in Week 4 (RBAC).
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** Password (BCrypt-encrypted), ignored during serialization to avoid leakage. */
    @JsonIgnore
    private String password;

    private String nickname;

    private String email;

    /** Status: 1 enabled, 0 disabled. */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** Logical delete: 0 not deleted, 1 deleted. */
    @TableLogic
    private Integer deleted;
}
