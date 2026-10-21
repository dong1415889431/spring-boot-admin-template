package com.dongh.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongh.admin.common.ResultCode;
import com.dongh.admin.common.exception.BusinessException;
import com.dongh.admin.dto.ArticleRequest;
import com.dongh.admin.entity.Article;
import com.dongh.admin.mapper.ArticleMapper;
import com.dongh.admin.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 文章服务实现（通用 CRUD 示例）
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public IPage<Article> pageArticles(long page, long size, String keyword, Integer status) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Article::getTitle, keyword)
                    .or().like(Article::getAuthor, keyword));
        }
        if (status != null) {
            wrapper.eq(Article::getStatus, status);
        }
        wrapper.orderByDesc(Article::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Article createArticle(ArticleRequest request) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setAuthor(request.getAuthor());
        article.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        save(article);
        return article;
    }

    @Override
    public Article updateArticle(Long id, ArticleRequest request) {
        Article article = getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setAuthor(request.getAuthor());
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }
        updateById(article);
        return article;
    }

    @Override
    public void deleteArticle(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        removeById(id);
    }
}
