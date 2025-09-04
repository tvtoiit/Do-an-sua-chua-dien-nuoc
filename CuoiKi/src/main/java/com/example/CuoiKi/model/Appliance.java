package com.example.CuoiKi.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "Appliances")
public class Appliance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ApplianceId")
	private Integer applianceId;

	@Column(name = "ApplianceName", nullable = false, columnDefinition = "nvarchar(max)")
	private String applianceName;

	@Column(name = "Brand", columnDefinition = "nvarchar(max)")
	private String brand;

	@Column(name = "Model", columnDefinition = "nvarchar(max)")
	private String model;

	@ManyToOne
	@JoinColumn(name = "TechnicianId", nullable = true)
	private Technician technician;

	@Column(name = "ImageUrl")
	private String imageUrl;

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

	public Integer getApplianceId() {
		return applianceId;
	}

	public void setApplianceId(Integer applianceId) {
		this.applianceId = applianceId;
	}

	public String getApplianceName() {
		return applianceName;
	}

	public void setApplianceName(String applianceName) {
		this.applianceName = applianceName;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Technician getTechnician() {
		return technician;
	}

	public void setTechnician(Technician technician) {
		this.technician = technician;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
