package com.example.CuoiKi.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.example.CuoiKi.repository.UserRepository;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

	@Autowired
	private TechnicianRepository technicianRepository;

	@GetMapping
	public List<Technician> getAll() {
		return technicianRepository.findByDeleteFlg(false);
	}

	@PostMapping
	public Technician create(@RequestBody Technician technician) {
		return technicianRepository.save(technician);
	}

	@PutMapping("/{id}")
	public Technician update(@PathVariable Integer id, @RequestBody Technician technician) {
		technician.setTechnicianId(id);
		technician.setUpdateAt(LocalDateTime.now());
		return technicianRepository.save(technician);
	}

	@PutMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		Optional<Technician> optionalTechnician = technicianRepository.findById(id);
		if (optionalTechnician.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Technician technician = optionalTechnician.get();
		technician.setDeleteAt(LocalDateTime.now());
		technician.setDeleteFlg(true);
		return ResponseEntity.ok(technicianRepository.save(technician));
	}
	/*
	 * @DeleteMapping("/{id}") public void delete(@PathVariable Integer id) {
	 * technicianRepository.deleteById(id); }
	 */
}
