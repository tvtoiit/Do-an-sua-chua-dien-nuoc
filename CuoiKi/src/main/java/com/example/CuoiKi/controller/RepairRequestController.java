package com.example.CuoiKi.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.CuoiKi.dto.RepairRequestDTO;
import com.example.CuoiKi.dto.RepairRequestTimingDTO;
import com.example.CuoiKi.model.RepairRequest;
import com.example.CuoiKi.model.RepairService;
import com.example.CuoiKi.model.Technician;
import com.example.CuoiKi.model.User;
import com.example.CuoiKi.repository.RepairRequestRepository;
import com.example.CuoiKi.repository.RepairServiceRepository;
import com.example.CuoiKi.repository.TechnicianRepository;
import com.example.CuoiKi.service.RepairRequestService;
import com.example.CuoiKi.service.UserService;

@RestController
@RequestMapping("/api/repair-requests")
public class RepairRequestController {
	private final RepairRequestService repairRequestService;
	private final UserService userService;

	@Autowired
	private RepairServiceRepository repairServiceRepository;

	@Autowired
	private RepairRequestRepository repairRequestRepository;

	@Autowired
	private TechnicianRepository technicianRepository;

	public RepairRequestController(RepairRequestService repairRequestService, UserService userService) {
		this.repairRequestService = repairRequestService;
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<?> createRepairRequest(@RequestBody RepairRequestDTO dto) {
		try {
			RepairRequest request = new RepairRequest();

			User customer = new User();
			customer.setUserId(dto.getCustomerId());
			request.setCustomer(customer);

			// ✅ Lấy Service từ DB để truy cập appliance
			RepairService service = repairServiceRepository.findById(dto.getServiceId())
					.orElseThrow(() -> new IllegalArgumentException("Dịch vụ không tồn tại"));

			request.setService(service);
			request.setAppliance(service.getAppliance()); // ✅ Gán thiết bị từ service

			request.setAppointmentDate(LocalDateTime.parse(dto.getAppointmentDate()));
			request.setNotes(dto.getNotes());

			repairRequestService.createRepairRequest(request);
			return ResponseEntity.ok("Tạo yêu cầu sửa chữa thành công");

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (DateTimeParseException e) {
			return ResponseEntity.badRequest().body("Định dạng ngày hẹn không hợp lệ.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi: " + e.getMessage());
		}
	}

	@GetMapping("/{customerId}")
	public ResponseEntity<?> getCustomerRequests(@PathVariable Integer customerId) {
		// Kiểm tra xem customerId có tồn tại không
		if (!userService.customerExists(customerId)) {
			return ResponseEntity.status(404).body("Customer ID không tồn tại!");
		}

		List<RepairRequest> requests = repairRequestService.getCustomerRequests(customerId);
		return ResponseEntity.ok(requests);
	}

	@GetMapping("/details/{requestId}")
	public ResponseEntity<RepairRequest> getRequestDetails(@PathVariable Integer requestId) {
		Optional<RepairRequest> request = repairRequestService.getRequestById(requestId);
		return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{requestId}/status")
	public ResponseEntity<RepairRequest> updateRequestStatus(@PathVariable Integer requestId,
			@RequestParam String status, @RequestParam(required = false) String rejectReason,
			@RequestParam(required = false) Integer technicianId) {
		RepairRequest updatedRequest = repairRequestService.updateRequestStatus(requestId, status, rejectReason,
				technicianId);
		if (updatedRequest != null) {
			return ResponseEntity.ok(updatedRequest);
		}
		return ResponseEntity.notFound().build();
	}

	@PutMapping("/{requestId}/cancel")
	public ResponseEntity<?> cancelRepairRequest(@PathVariable Integer requestId, @RequestParam Integer customerId) {
		try {
			return ResponseEntity.ok(repairRequestService.cancelRepairRequest(requestId, customerId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/all")
	public ResponseEntity<List<RepairRequest>> getAllRequests() {
		return ResponseEntity.ok(repairRequestRepository.findAll());
	}

	@GetMapping("/technician/{userId}")
	public ResponseEntity<?> getRequestsByTechnicianId(
	        @PathVariable Integer userId,
	        @RequestParam(required = false) List<String> status) {

	    Optional<Technician> optionalTechnician = technicianRepository.findByUser_UserId(userId);
	    if (optionalTechnician.isPresent()) {
	        Integer technicianId = optionalTechnician.get().getTechnicianId();
	        List<RepairRequest> requests;

	        if (status != null && !status.isEmpty()) {
	            requests = repairRequestRepository.findByTechnicianIdAndStatusIn(technicianId, status);
	        } else {
	            requests = repairRequestRepository.findByTechnicianId(technicianId);
	        }

	        return ResponseEntity.ok(requests);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Không tìm thấy thợ sữa chữa với id: " + userId);
	    }
	}


	@GetMapping("/technician")
	public ResponseEntity<?> getUnassignedRepairRequests(@RequestParam(required = false) String status) {
		List<RepairRequest> requests;

		if (status != null) {
			requests = repairRequestRepository.findByTechnicianIdIsNullAndStatus(status);
		} else {
			requests = repairRequestRepository.findByTechnicianIdIsNull();
		}

		return ResponseEntity.ok(requests);
	}

	@GetMapping("/user2/{userId}")
	public ResponseEntity<?> getRequestsByUserId(@PathVariable Integer userId,
			@RequestParam(required = false) String status) {
		Optional<Technician> optionalTechnician = technicianRepository.findByUser_UserId(userId);
		if (optionalTechnician.isPresent()) {
			Integer technicianId = optionalTechnician.get().getTechnicianId();
			List<RepairRequest> requests;

			if (status != null) {
				requests = repairRequestRepository.findByTechnicianIdAndStatus(technicianId, status);
			} else {
				requests = repairRequestRepository.findByTechnicianId(technicianId);
			}

			return ResponseEntity.ok(requests);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Technician not found for userId: " + userId);
		}
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<RepairRequest>> getRepairRequestsByCustomer(@PathVariable Integer customerId) {
		List<RepairRequest> requests = repairRequestService.getCustomerRequests(customerId);
		return ResponseEntity.ok(requests);
	}

	@GetMapping("/completed/{customerId}")
	public ResponseEntity<List<RepairRequest>> getCompletedRequestsByCustomer(@PathVariable Integer customerId) {
		List<RepairRequest> completedRequests = repairRequestService.getCompletedRequestsByCustomer(customerId);
		return ResponseEntity.ok(completedRequests);
	}

	@PutMapping("/{requestId}/timing-progress")
	public ResponseEntity<?> updateTimingAndProgress(@PathVariable Integer requestId,
			@RequestBody RepairRequestTimingDTO timingDTO) {

		Optional<RepairRequest> optionalRequest = repairRequestRepository.findById(requestId);
		if (optionalRequest.isEmpty()) {
			return ResponseEntity.status(404).body("RepairRequest không tồn tại.");
		}

		RepairRequest request = optionalRequest.get();

		// Cập nhật các thông tin liên quan
		if (timingDTO.getStartTime() != null) {
			request.setStartTime(timingDTO.getStartTime());
		}

		if (timingDTO.getDurationMinutes() != null) {
			request.setDurationMinutes(timingDTO.getDurationMinutes());
		}

		if (timingDTO.getExtensions() != null) {
			request.setExtensions(timingDTO.getExtensions());
		}

		if (timingDTO.getProgress() != null) {
			request.setProgress(timingDTO.getProgress());
		}

		// Lưu công việc sau khi cập nhật
		repairRequestRepository.save(request);
		return ResponseEntity.ok("Cập nhật thời gian và tiến độ thành công.");
	}

	@PutMapping("/{requestId}/start")
	public ResponseEntity<?> updateStartTimeAndDuration(@PathVariable Integer requestId,
			@RequestBody RepairRequestTimingDTO timingDTO) {

		Optional<RepairRequest> optionalRequest = repairRequestRepository.findById(requestId);
		if (optionalRequest.isEmpty()) {
			return ResponseEntity.status(404).body("RepairRequest không tồn tại.");
		}

		RepairRequest request = optionalRequest.get();

		// Kiểm tra trạng thái công việc
		String currentStatus = request.getStatus();
		if ("in-progress".equalsIgnoreCase(currentStatus) || "Completed".equalsIgnoreCase(currentStatus)) {
			return ResponseEntity.status(400).body("Công việc đã bắt đầu hoặc đã hoàn thành, không thể bắt đầu lại.");
		}

		// Cập nhật thời gian bắt đầu và thời gian dự kiến
		if (timingDTO.getStartTime() != null) {
			request.setStartTime(timingDTO.getStartTime());
		}

		if (timingDTO.getDurationMinutes() != null) {
			request.setDurationMinutes(timingDTO.getDurationMinutes());
		}

		request.setStatus("in-progress");

		repairRequestRepository.save(request);
		return ResponseEntity.ok("Bắt đầu công việc thành công.");
	}

	@PutMapping("/{requestId}/extend")
	public ResponseEntity<?> extendDuration(@PathVariable Integer requestId,
			@RequestBody RepairRequestTimingDTO timingDTO) {

		Optional<RepairRequest> optionalRequest = repairRequestRepository.findById(requestId);
		if (optionalRequest.isEmpty()) {
			return ResponseEntity.status(404).body("RepairRequest không tồn tại.");
		}

		RepairRequest request = optionalRequest.get();

		// Gia hạn thời gian nếu có
		if (timingDTO.getExtensions() != null) {
			int currentExtensions = request.getExtensions() != null ? request.getExtensions() : 0;
			request.setExtensions(currentExtensions + timingDTO.getExtensions());
			System.out.println(currentExtensions + timingDTO.getExtensions());
		}

		repairRequestRepository.save(request);
		return ResponseEntity.ok("Gia hạn thời gian thành công.");
	}

	@PutMapping("/{requestId}/progress")
	public ResponseEntity<?> updateProgress(@PathVariable Integer requestId,
			@RequestBody RepairRequestTimingDTO timingDTO) {

		Optional<RepairRequest> optionalRequest = repairRequestRepository.findById(requestId);
		if (optionalRequest.isEmpty()) {
			return ResponseEntity.status(404).body("RepairRequest không tồn tại.");
		}

		RepairRequest request = optionalRequest.get();

		// Cập nhật tiến độ nếu có
		if (timingDTO.getProgress() != null) {
			request.setProgress(timingDTO.getProgress());
		}

		repairRequestRepository.save(request);
		return ResponseEntity.ok("Cập nhật tiến độ thành công.");
	}

	@GetMapping("/technician/{technicianId}/in-progress")
	public ResponseEntity<?> getInProgressJobs(@PathVariable Integer technicianId) {
		List<RepairRequest> inProgressRequests = repairRequestRepository.findByTechnicianIdAndStatus(technicianId,
				"in-progress");

		if (inProgressRequests.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyList());
		}

		return ResponseEntity.ok(inProgressRequests);
	}

}