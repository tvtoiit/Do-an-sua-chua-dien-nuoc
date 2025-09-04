package com.example.CuoiKi.controller;

import com.example.CuoiKi.model.Article;
import com.example.CuoiKi.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
	private final ArticleService articleService;

	public ArticleController(ArticleService articleService) {
		this.articleService = articleService;
	}

	@PostMapping
	public ResponseEntity<Article> submitArticle(@RequestBody Article article) {
		return ResponseEntity.ok(articleService.submitArticle(article));
	}

	@GetMapping("/approved")
	public ResponseEntity<List<Article>> getApprovedArticles() {
		return ResponseEntity.ok(articleService.getApprovedArticles());
	}

	@GetMapping
	public ResponseEntity<List<Article>> getAllArticles() {
		return ResponseEntity.ok(articleService.getAllArticles());
	}

	@PutMapping("/approve/{articleId}")
	public ResponseEntity<?> approveArticle(@PathVariable Long articleId) {
		Optional<Article> approvedArticle = articleService.approveArticle(articleId);
		return approvedArticle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/reject/{articleId}")
	public ResponseEntity<?> rejectArticle(@PathVariable Long articleId) {
		Optional<Article> approvedArticle = articleService.rejectArticle(articleId);
		return approvedArticle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/{articleId}")
	public ResponseEntity<Article> getArticleById(@PathVariable Long articleId) {
		Optional<Article> article = articleService.getArticleById(articleId);
		return article.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Article>> getArticlesByUserId(@PathVariable Integer userId) {
		List<Article> articles = articleService.getArticlesByUserId(userId);
		return ResponseEntity.ok(articles);
	}

}
