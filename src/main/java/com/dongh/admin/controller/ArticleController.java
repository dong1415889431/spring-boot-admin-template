package com.dongh.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongh.admin.common.Result;
import com.dongh.admin.dto.ArticleRequest;
import com.dongh.admin.entity.Article;
import com.dongh.admin.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Article management endpoints (generic CRUD example)
 */
@Tag(name = "Article Management", description = "Generic CRUD example: pagination / detail / create, update, delete")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "Paginated article query")
    @PreAuthorize("hasAuthority('article:list')")
    @GetMapping
    public Result<IPage<Article>> page(@RequestParam(defaultValue = "1") long page,
                                       @RequestParam(defaultValue = "10") long size,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Integer status) {
        return Result.success(articleService.pageArticles(page, size, keyword, status));
    }

    @Operation(summary = "Article detail")
    @PreAuthorize("hasAuthority('article:list')")
    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        return Result.success(articleService.getById(id));
    }

    @Operation(summary = "Create article")
    @PreAuthorize("hasAuthority('article:create')")
    @PostMapping
    public Result<Article> create(@Valid @RequestBody ArticleRequest request) {
        return Result.success(articleService.createArticle(request));
    }

    @Operation(summary = "Update article")
    @PreAuthorize("hasAuthority('article:update')")
    @PutMapping("/{id}")
    public Result<Article> update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        return Result.success(articleService.updateArticle(id, request));
    }

    @Operation(summary = "Delete article")
    @PreAuthorize("hasAuthority('article:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }
}
