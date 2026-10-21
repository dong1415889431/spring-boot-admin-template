package com.dongh.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Article create / update request.
 */
@Data
public class ArticleRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String content;

    private String author;

    /** Status: 1 published, 0 draft. */
    private Integer status;
}
