package com.dongh.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dongh.admin.dto.ArticleRequest;
import com.dongh.admin.entity.Article;

/**
 * Article service (generic CRUD example)
 */
public interface ArticleService extends IService<Article> {

    /** Paginated article query (keyword matches title/author, filterable by status) */
    IPage<Article> pageArticles(long page, long size, String keyword, Integer status);

    Article createArticle(ArticleRequest request);

    Article updateArticle(Long id, ArticleRequest request);

    void deleteArticle(Long id);
}
