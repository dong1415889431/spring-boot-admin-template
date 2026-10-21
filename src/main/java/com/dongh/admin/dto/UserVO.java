package com.dongh.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User list view object (includes role info).
 */
@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    /** Status: 1 enabled, 0 disabled. */
    private Integer status;

    private LocalDateTime createdAt;

    /** Role IDs assigned to the user. */
    private List<Long> roleIds;

    /** Role names assigned to the user. */
    private List<String> roleNames;
}
