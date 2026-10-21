package com.dongh.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dongh.admin.dto.ArticleRequest;
import com.dongh.admin.entity.Article;

/**
 * 文章服务（通用 CRUD 示例）
 */
public interface ArticleService extends IService<Article> {

    /** 分页查询文章（关键字匹配标题/作者，可按状态过滤） */
    IPage<Article> pageArticles(long page, long size, String keyword, Integer status);

    Article createArticle(ArticleRequest request);

    Article updateArticle(Long id, ArticleRequest request);

    void deleteArticle(Long id);
}
