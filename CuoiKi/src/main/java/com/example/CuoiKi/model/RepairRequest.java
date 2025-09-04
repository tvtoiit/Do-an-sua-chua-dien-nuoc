package com.example.CuoiKi.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "RepairRequests")
public class RepairRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RequestId")
	private Integer requestId;

	@ManyToOne
	@JoinColumn(name = "CustomerId", nullable = false)
	private User customer;

	@Column(name = "TechnicianId")
	private Integer technicianId;

	@ManyToOne
	@JoinColumn(name = "ServiceId", nullable = false)
	private RepairService service; // Một dịch vụ duy nhất

	@ManyToOne
	@JoinColumn(name = "ApplianceId", nullable = true)
	private Appliance appliance; // Một thiết bị duy nhất

	@Column(name = "AppointmentDate", nullable = false)
	private LocalDateTime appointmentDate;

	@Column(name = "Status", nullable = false)
	private String status;

	@Column(name = "Notes", columnDefinition = "nvarchar(max)")
	private String notes;

	@Column(name = "RejectReason", columnDefinition = "nvarchar(max)")
	private String rejectReason;

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

	@Column(name = "StartTime")
	private OffsetDateTime startTime;

	@Column(name = "DurationMinutes")
	private Integer durationMinutes;

	@Column(name = "Extensions")
	private Integer extensions;

	@Column(name = "Progress")
	private Integer progress; // giá trị từ 0 đến 100

	public RepairRequest() {
		this.createdAt = LocalDateTime.now();
		this.status = "Pending"; // Mặc định trạng thái khi tạo mới
	}

	// Getter và Setter
	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public Integer getTechnicianId() {
		return technicianId;
	}

	public void setTechnicianId(Integer technicianId) {
		this.technicianId = technicianId;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public RepairService getService() {
		return service;
	}

	public void setService(RepairService service) {
		this.service = service;
	}

	public Appliance getAppliance() {
		return appliance;
	}

	public void setAppliance(Appliance appliance) {
		this.appliance = appliance;
	}

	public LocalDateTime getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(LocalDateTime appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
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

	public OffsetDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(OffsetDateTime startTime) {
		this.startTime = startTime;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public Integer getExtensions() {
		return extensions;
	}

	public void setExtensions(Integer extensions) {
		this.extensions = extensions;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

}
