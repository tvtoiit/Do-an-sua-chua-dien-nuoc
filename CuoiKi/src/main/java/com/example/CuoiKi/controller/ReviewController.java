package com.example.CuoiKi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CuoiKi.dto.ReviewRequest;
import com.example.CuoiKi.model.Review;
import com.example.CuoiKi.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@PostMapping
	public ResponseEntity<?> addReview(@RequestBody ReviewRequest reviewRequest) {
		try {
			return ResponseEntity.ok(reviewService.addReview(reviewRequest.getCustomerId(),
					reviewRequest.getServiceId(), reviewRequest.getRating(), reviewRequest.getComment()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/service/{serviceId}")
	public ResponseEntity<List<Review>> getReviewsByService(@PathVariable Integer serviceId) {
		try {
			return ResponseEntity.ok(reviewService.getReviewsByService(serviceId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
}
