package com.example.CuoiKi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
@Table(name = "Users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UserId")
	private Integer userId;

	@Column(name = "FullName", nullable = false, columnDefinition = "nvarchar(max)")
	private String fullName;

	@Column(name = "Email", nullable = true, unique = true)
	private String email;

	@Column(name = "PhoneNumber", nullable = true)
	private String phoneNumber;

	@Column(name = "Address", nullable = true, columnDefinition = "nvarchar(max)")
	private String address;

	@Transient // Không lưu vào DB
	private String password; // Nhận password từ request

	@Column(name = "PasswordHash", nullable = false)
	private String passwordHash;

	@Column(name = "Role", nullable = false)
	private String role;

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

	// Getters and Setters
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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
