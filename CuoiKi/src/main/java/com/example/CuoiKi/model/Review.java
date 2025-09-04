package com.example.CuoiKi.model;

import java.time.LocalDateTime;

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
@Table(name = "Reviews")
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ReviewId")
	private Integer reviewId;

	@ManyToOne
	@JoinColumn(name = "CustomerId", nullable = false)
	private User customer;

	@ManyToOne
	@JoinColumn(name = "TechnicianId", nullable = true)
	private Technician technician;

	@ManyToOne
	@JoinColumn(name = "ServiceId", nullable = false)
	private RepairService service;

	@Column(name = "Rating", nullable = false)
	private Integer rating;

	@Column(name = "Comment", columnDefinition = "nvarchar(max)")
	private String comment;

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

	public Review() {
		this.createdAt = LocalDateTime.now();
	}

	public Integer getReviewId() {
		return reviewId;
	}

	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public Technician getTechnician() {
		return technician;
	}

	public void setTechnician(Technician technician) {
		this.technician = technician;
	}

	public RepairService getService() {
		return service;
	}

	public void setService(RepairService service) {
		this.service = service;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
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

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getDeleteFlg() {
		return deleteFlg;
	}

	public void setDeleteFlg(Boolean deleteFlg) {
		this.deleteFlg = deleteFlg;
	}

}
