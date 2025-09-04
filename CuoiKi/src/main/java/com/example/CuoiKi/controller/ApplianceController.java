package com.example.CuoiKi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CuoiKi.model.Appliance;
import com.example.CuoiKi.model.Technician;
import com.example.CuoiKi.repository.TechnicianRepository;
import com.example.CuoiKi.service.ApplianceService;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/appliances")
public class ApplianceController {

	@Autowired
	private ApplianceService applianceService;

	@Autowired
	private TechnicianRepository technicianRepository;

	// Lấy tất cả thiết bị
	@GetMapping
	public List<Appliance> getAllAppliances() {
		return applianceService.getAllAppliances();
	}

	// Lấy thiết bị theo ID
	@GetMapping("/{id}")
	public ResponseEntity<?> getApplianceById(@PathVariable Integer id) {
		return applianceService.getApplianceById(id).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Thêm thiết bị
	@PostMapping
	public ResponseEntity<Appliance> createAppliance(@RequestBody Appliance appliance) {
		// Kiểm tra xem Technician có tồn tại trong request hay không
		if (appliance.getTechnician() != null && appliance.getTechnician().getTechnicianId() != null) {
			// Nếu Technician đã có ID (tồn tại trong DB), lấy technician từ DB
			Optional<Technician> technicianOpt = technicianRepository
					.findById(appliance.getTechnician().getTechnicianId());

			if (technicianOpt.isEmpty()) {
				// Nếu Technician không tồn tại trong DB, trả về lỗi 400 nhưng vẫn trả về
				// ResponseEntity<Appliance>
				return ResponseEntity.badRequest().body(null); // Hoặc có thể trả về đối tượng lỗi tùy thích
			}

			// Gán Technician vào Appliance
			appliance.setTechnician(technicianOpt.get());
		} else {
			// Nếu không có Technician, gán Technician = null
			appliance.setTechnician(null);
		}
		appliance.setCreatedAt(LocalDateTime.now());

		// Lưu Appliance
		Appliance savedAppliance = applianceService.saveAppliance(appliance);

		return ResponseEntity.ok(savedAppliance); // Trả về Appliance đã được lưu
	}

	// Cập nhật thiết bị
	@PutMapping("/{id}")
	public ResponseEntity<?> updateAppliance(@PathVariable Integer id, @RequestBody Appliance updatedAppliance) {
		Optional<Appliance> optionalAppliance = applianceService.getApplianceById(id);
		if (optionalAppliance.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Appliance appliance = optionalAppliance.get();

		// Gán các thuộc tính thông thường
		appliance.setApplianceName(updatedAppliance.getApplianceName());
		appliance.setBrand(updatedAppliance.getBrand());
		appliance.setModel(updatedAppliance.getModel());
		appliance.setImageUrl(updatedAppliance.getImageUrl());

		// Lấy technician từ DB (nếu có) để đảm bảo là entity được quản lý
		if (updatedAppliance.getTechnician() != null && updatedAppliance.getTechnician().getTechnicianId() != null) {

			Optional<Technician> technicianOpt = technicianRepository
					.findById(updatedAppliance.getTechnician().getTechnicianId());

			if (technicianOpt.isEmpty()) {
				return ResponseEntity.badRequest().body("Technician không tồn tại");
			}

			appliance.setTechnician(technicianOpt.get());
		} else {
			appliance.setTechnician(null);
		}
		appliance.setUpdateAt(LocalDateTime.now());

		return ResponseEntity.ok(applianceService.saveAppliance(appliance));
	}

	// Xoá mềm thiết bị - cập nhật delete_flg = 1
	@PutMapping("/delete/{id}")
	public ResponseEntity<?> deleteAppliance(@PathVariable Integer id) {
		if (!applianceService.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		applianceService.deleteAppliance(id);
		return ResponseEntity.ok("Xoá mềm thiết bị thành công (delete_flg = 1)");
	}

}
