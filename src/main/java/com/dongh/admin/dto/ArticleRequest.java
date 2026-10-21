package com.dongh.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文章创建 / 更新请求
 */
@Data
public class ArticleRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private String author;

    /** 状态：1 发布，0 草稿 */
    private Integer status;
}
