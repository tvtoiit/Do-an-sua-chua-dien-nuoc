package com.example.CuoiKi.service;

import com.example.CuoiKi.model.Appliance;
import com.example.CuoiKi.repository.ApplianceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplianceService {

	@Autowired
	private ApplianceRepository applianceRepository;

	public List<Appliance> getAllAppliances() {
		return applianceRepository.findByDeleteFlg(false);
	}

	public Optional<Appliance> getApplianceById(Integer id) {
		return applianceRepository.findById(id);
	}

	public Appliance saveAppliance(Appliance appliance) {
		return applianceRepository.save(appliance);
	}

	public void deleteAppliance(Integer id) {
		Optional<Appliance> applianceOpt = applianceRepository.findById(id);
		applianceOpt.ifPresent(appliance -> {
			appliance.setDeleteFlg(true);
			appliance.setDeleteAt(LocalDateTime.now());
			applianceRepository.save(appliance);
		});
	}

	public boolean existsById(Integer id) {
		return applianceRepository.existsById(id);
	}
}
