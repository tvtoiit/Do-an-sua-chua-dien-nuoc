package com.example.CuoiKi.service;

import com.example.CuoiKi.model.Article;
import com.example.CuoiKi.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.CuoiKi.ai.OpenAIApiService;
import org.json.JSONObject;

@Service
public class ArticleService {
	private final ArticleRepository articleRepository;

	private final OpenAIApiService openAIApiService;

	public ArticleService(ArticleRepository articleRepository, OpenAIApiService openAIApiService) {
		this.articleRepository = articleRepository;
		this.openAIApiService = openAIApiService;
	}

	public Article submitArticle(Article article) {
		LocalDateTime now = LocalDateTime.now();
		article.setCreatedAt(now);

		try {
			// Gửi nội dung đến AI kiểm duyệt
			String aiResponse = openAIApiService.moderateArticle(article.getTitle(), article.getDescription(),
					article.getContent());

			// Parse phản hồi JSON từ AI
			JSONObject result = new JSONObject(aiResponse);
			String status = result.getString("result");
			String reason = result.optString("reason", "");

			if ("Approved".equalsIgnoreCase(status)) {
				article.setStatus("Approved");
				article.setReason("Bài viết đã được hệ thống duyệt tự động.");
			} else {
				article.setStatus("Rejected");
				article.setReason("Từ chối bởi AI: " + reason);
			}


		} catch (Exception e) {
			// Trong trường hợp lỗi (hết quota, lỗi mạng, lỗi JSON...), đặt trạng thái chờ
			// duyệt
			article.setStatus("Pending");
			article.setReason("Không thể kiểm duyệt tự động: " + e.getMessage());
		}
		return articleRepository.save(article);
	}

	public List<Article> getApprovedArticles() {
		return articleRepository.findByStatus("Approved");
	}

	public List<Article> getAllArticles() {
		return articleRepository.findAll();
	}

	public Optional<Article> approveArticle(Long articleId) {
		Optional<Article> articleOpt = articleRepository.findById(articleId);
		if (articleOpt.isPresent()) {
			Article article = articleOpt.get();
			article.setStatus("Approved");
			articleRepository.save(article);
			return Optional.of(article);
		}
		return Optional.empty();
	}

	public Optional<Article> rejectArticle(Long articleId) {
		Optional<Article> articleOpt = articleRepository.findById(articleId);
		if (articleOpt.isPresent()) {
			Article article = articleOpt.get();
			article.setStatus("Rejected");
			articleRepository.save(article);
			return Optional.of(article);
		}
		return Optional.empty();
	}

	public Optional<Article> getArticleById(Long articleId) {
		return articleRepository.findById(articleId);
	}

	public List<Article> getArticlesByUserId(Integer userId) {
		return articleRepository.findByAuthor_UserId(userId);
	}

}
