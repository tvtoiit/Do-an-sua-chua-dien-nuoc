package com.example.CuoiKi.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "Technicians")
public class Technician {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TechnicianId")
	private Integer technicianId;

	@OneToOne
	@JoinColumn(name = "UserId", nullable = false)
	private User user;

	@Column(name = "ExperienceYears")
	private Integer experienceYears;

	@Column(name = "Qualification")
	private String qualification;

	@Column(name = "HealthCertificate")
	private String healthCertificate;

	@Column(name = "CreatedAt")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "UpdateAt")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime updateAt;

	@Column(name = "DeleteAt")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime deleteAt;

	@Column(name = "DeleteFlg")
	private Boolean deleteFlg = false;

	public Technician() {
	}

	public Integer getTechnicianId() {
		return technicianId;
	}

	public void setTechnicianId(Integer technicianId) {
		this.technicianId = technicianId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getExperienceYears() {
		return experienceYears;
	}

	public void setExperienceYears(Integer experienceYears) {
		this.experienceYears = experienceYears;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getHealthCertificate() {
		return healthCertificate;
	}

	public void setHealthCertificate(String healthCertificate) {
		this.healthCertificate = healthCertificate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	public LocalDateTime getDeleteAt() {
		return deleteAt;
	}

	public void setDeleteAt(LocalDateTime deleteAt) {
		this.deleteAt = deleteAt;
	}

	public Boolean getDeleteFlg() {
		return deleteFlg;
	}

	public void setDeleteFlg(Boolean deleteFlg) {
		this.deleteFlg = deleteFlg;
	}

}
