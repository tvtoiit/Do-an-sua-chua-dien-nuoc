package com.example.CuoiKi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CuoiKi.model.Appliance;

public interface ApplianceRepository extends JpaRepository<Appliance, Integer> {
	List<Appliance> findByDeleteFlg(Boolean deleteFlg);
}
