package com.example.CuoiKi.dto;

public class RepairServiceRequest {
	private Integer applianceId;
	private String serviceName;
	private Double price;
	private String description;
	private String imageUrl; // Thêm trường URL ảnh

	// Getter và Setter
	public Integer getApplianceId() {
		return applianceId;
	}

	public void setApplianceId(Integer applianceId) {
		this.applianceId = applianceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
