package com.example.CuoiKi.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.CuoiKi.dto.RepairServiceRequest;
import com.example.CuoiKi.dto.RepairServiceSimpleDTO;
import com.example.CuoiKi.model.Appliance;
import com.example.CuoiKi.model.RepairService;
import com.example.CuoiKi.repository.ApplianceRepository;
import com.example.CuoiKi.repository.RepairServiceRepository;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

	@Autowired
	private RepairServiceRepository serviceRepository;

	@Autowired
	private ApplianceRepository applianceRepository;

	// ✅ Đơn giản hóa danh sách
	@GetMapping("/simple")
	public List<RepairServiceSimpleDTO> getSimpleServices() {
		return serviceRepository.findAll().stream()
				.map(service -> new RepairServiceSimpleDTO(service.getServiceId(), service.getServiceName()))
				.collect(Collectors.toList());
	}

	// ✅ Xem tất cả hoặc lọc theo ApplianceId
	@GetMapping
	public List<RepairService> getAllServices(
			@RequestParam(name = "applianceId", required = false) Integer applianceId) {
		if (applianceId != null) {
			return serviceRepository.findByAppliance_ApplianceId(applianceId);
		}
		return serviceRepository.findByDeleteFlg(false);
	}

	// ✅ Xem chi tiết
	@GetMapping("/{id}")
	public ResponseEntity<?> getServiceById(@PathVariable Integer id) {
		Optional<RepairService> service = serviceRepository.findById(id);
		return service.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// ✅ Thêm mới
	@PostMapping
	public ResponseEntity<?> createService(@RequestBody RepairServiceRequest request) {
		Optional<Appliance> applianceOpt = applianceRepository.findById(request.getApplianceId());
		if (applianceOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Appliance not found");
		}

		RepairService service = new RepairService();
		service.setAppliance(applianceOpt.get());
		service.setServiceName(request.getServiceName());
		service.setPrice(request.getPrice());
		service.setDescription(request.getDescription());
		service.setImageUrl(request.getImageUrl()); // Set URL ảnh
		serviceRepository.save(service);

		return ResponseEntity.ok(service);
	}

	// ✅ Cập nhật
	@PutMapping("/{id}")
	public ResponseEntity<?> updateService(@PathVariable Integer id, @RequestBody RepairServiceRequest request) {
		Optional<RepairService> existing = serviceRepository.findById(id);
		if (existing.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Optional<Appliance> applianceOpt = applianceRepository.findById(request.getApplianceId());
		if (applianceOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Appliance not found");
		}

		RepairService service = existing.get();
		service.setAppliance(applianceOpt.get());
		service.setServiceName(request.getServiceName());
		service.setPrice(request.getPrice());
		service.setDescription(request.getDescription());
		service.setImageUrl(request.getImageUrl()); // Cập nhật URL ảnh
		service.setUpdateAt(LocalDateTime.now());
		serviceRepository.save(service);

		return ResponseEntity.ok(service);
	}

	// ✅ Xoá
	/*
	 * @DeleteMapping("/{id}") public ResponseEntity<?> deleteService(@PathVariable
	 * Integer id) { if (!serviceRepository.existsById(id)) { return
	 * ResponseEntity.notFound().build(); } serviceRepository.deleteById(id); return
	 * ResponseEntity.ok("Service deleted successfully"); }
	 */
	@PutMapping("/delete/{id}")
	public ResponseEntity<?> deleteService(@PathVariable Integer id) {
		Optional<RepairService> existing = serviceRepository.findById(id);
		if (existing.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		RepairService service = existing.get();
		service.setDeleteFlg(true);
		service.setDeleteAt(LocalDateTime.now());
		serviceRepository.save(service);

		return ResponseEntity.ok(service);
	}

}
