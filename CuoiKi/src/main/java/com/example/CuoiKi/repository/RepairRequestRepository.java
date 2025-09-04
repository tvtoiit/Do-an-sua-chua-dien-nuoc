package com.example.CuoiKi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.CuoiKi.model.RepairRequest;

public interface RepairRequestRepository extends JpaRepository<RepairRequest, Integer> {

	// Trả về danh sách yêu cầu sửa chữa theo customerId
	List<RepairRequest> findByCustomer_UserId(Integer customerId);

	// Kiểm tra xem yêu cầu sửa chữa với dịch vụ và trạng thái đã tồn tại chưa
	@Query("""
			SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
			FROM RepairRequest r
			WHERE r.customer.userId = :customerId
			AND r.service.serviceId = :serviceId
			AND r.appliance.applianceId = :applianceId
			AND r.status = :status
			""")
	boolean existsByCustomerIdAndServiceIdAndApplianceIdAndStatus(@Param("customerId") Integer customerId,
			@Param("serviceId") Integer serviceId, @Param("applianceId") Integer applianceId,
			@Param("status") String status);

	List<RepairRequest> findByTechnicianId(Integer technicianId);

	List<RepairRequest> findByTechnicianIdAndStatus(Integer technicianId, String status);

	List<RepairRequest> findByCustomer_UserIdAndStatus(Integer customerId, String status);
	
	List<RepairRequest> findByTechnicianIdIsNull();

	List<RepairRequest> findByTechnicianIdIsNullAndStatus(String status);

	List<RepairRequest> findByTechnicianIdAndStatusIn(Integer technicianId, List<String> statuses);
	
	List<RepairRequest> findByDeleteFlg(Boolean deleteFlg);

}
