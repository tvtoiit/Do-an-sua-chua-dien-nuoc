package com.example.CuoiKi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.CuoiKi.model.RepairService;
import com.example.CuoiKi.model.Review;
import com.example.CuoiKi.model.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
	List<Review> findByService(RepairService service);

	List<Review> findByCustomer(User customer);

	boolean existsByCustomerAndService(User customer, RepairService service);

	List<Review> findByDeleteFlg(Boolean deleteFlg);

}
