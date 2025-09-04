package com.example.CuoiKi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.CuoiKi.model.RepairService;

public interface RepairServiceRepository extends JpaRepository<RepairService, Integer> {

	@Query("""
			    SELECT DISTINCT s
			    FROM RepairRequest rr
			    JOIN rr.service s
			    WHERE rr.customer.userId = :customerId
			""")
	List<RepairService> findServicesByCustomerId(@Param("customerId") Integer customerId);

	// ✅ Lấy danh sách dịch vụ theo thiết bị
	List<RepairService> findByAppliance_ApplianceId(Integer applianceId);

	List<RepairService> findByDeleteFlg(Boolean deleteFlg);
}
