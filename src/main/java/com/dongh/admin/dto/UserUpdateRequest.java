package com.dongh.admin.dto;

import lombok.Data;

/**
 * User update request (nickname / email).
 */
@Data
public class UserUpdateRequest {

    private String nickname;

    private String email;
}
