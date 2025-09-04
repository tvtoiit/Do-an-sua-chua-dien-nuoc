package com.example.CuoiKi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CuoiKi.model.Technician;

public interface TechnicianRepository extends JpaRepository<Technician, Integer> {
	Optional<Technician> findByUser_UserId(Integer userId);

	List<Technician> findByDeleteFlg(Boolean deleteFlg);

}
