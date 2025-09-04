package com.example.CuoiKi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.CuoiKi.model.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
	List<Article> findByStatus(String status);

	public List<Article> findByAuthor_UserId(Integer userId);

	List<Article> findByDeleteFlg(Boolean deleteFlg);
}
