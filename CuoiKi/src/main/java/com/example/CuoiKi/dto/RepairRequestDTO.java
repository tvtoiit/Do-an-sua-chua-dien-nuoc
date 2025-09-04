package com.example.CuoiKi.dto;

public class RepairRequestDTO {
	private Integer serviceId;
	private Integer applianceId;
	private String appointmentDate;
	private String notes;
	private Integer customerId;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getApplianceId() {
		return applianceId;
	}

	public void setApplianceId(Integer applianceId) {
		this.applianceId = applianceId;
	}

	public String getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

}
