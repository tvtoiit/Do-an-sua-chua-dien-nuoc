package com.example.CuoiKi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.CuoiKi.model.RepairService;
import com.example.CuoiKi.model.Review;
import com.example.CuoiKi.model.User;
import com.example.CuoiKi.repository.RepairRequestRepository;
import com.example.CuoiKi.repository.RepairServiceRepository;
import com.example.CuoiKi.repository.ReviewRepository;
import com.example.CuoiKi.repository.UserRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final RepairRequestRepository repairRequestRepository;
	private final UserRepository userRepository;
	private final RepairServiceRepository repairServiceRepository;

	public ReviewService(ReviewRepository reviewRepository, RepairRequestRepository repairRequestRepository,
			UserRepository userRepository, RepairServiceRepository repairServiceRepository) {
		this.reviewRepository = reviewRepository;
		this.repairRequestRepository = repairRequestRepository;
		this.userRepository = userRepository;
		this.repairServiceRepository = repairServiceRepository;
	}

	public Review addReview(Integer customerId, Integer serviceId, Integer rating, String comment) {
		Optional<User> customerOpt = userRepository.findById(customerId);
		Optional<RepairService> serviceOpt = repairServiceRepository.findById(serviceId);

		if (customerOpt.isEmpty() || serviceOpt.isEmpty()) {
			throw new IllegalArgumentException("Người dùng hoặc dịch vụ không tồn tại.");
		}

		User customer = customerOpt.get();
		RepairService service = serviceOpt.get();

		// Kiểm tra xem khách hàng có đặt lịch sửa chữa cho dịch vụ này không
		boolean hasCompletedService = repairRequestRepository.existsByCustomerIdAndServiceIdAndApplianceIdAndStatus(
				customer.getUserId(), service.getServiceId(), service.getAppliance().getApplianceId(), "Completed");

		if (!hasCompletedService) {
			throw new IllegalArgumentException("Bạn chỉ có thể đánh giá sau khi đã sử dụng dịch vụ.");
		}

		// Kiểm tra xem khách hàng đã đánh giá dịch vụ này chưa
		if (reviewRepository.existsByCustomerAndService(customer, service)) {
			throw new IllegalArgumentException("Bạn đã đánh giá dịch vụ này trước đó.");
		}

		Review review = new Review();
		review.setCustomer(customer);
		review.setService(service);
		review.setTechnician(service.getAppliance().getTechnician());
		review.setRating(rating);
		review.setComment(comment);

		return reviewRepository.save(review);
	}

	public List<Review> getReviewsByService(Integer serviceId) {
		Optional<RepairService> serviceOpt = repairServiceRepository.findById(serviceId);
		if (serviceOpt.isEmpty()) {
			throw new IllegalArgumentException("Dịch vụ không tồn tại.");
		}
		return reviewRepository.findByService(serviceOpt.get());
	}
}
