package com.example.CuoiKi.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.CuoiKi.model.User;
import com.example.CuoiKi.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// Bộ nhớ tạm lưu user đăng ký chờ xác thực
	private final Map<String, User> pendingUsers = new ConcurrentHashMap<>();

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User registerUser(User user) {
		if ("Khách hàng".equals(user.getRole())) {
			user.setRole("Customer");
		}
		if ("Thợ sửa chữa".equals(user.getRole())) {
			user.setRole("Technician");
		}

		if (user.getPassword() == null || user.getPassword().isBlank()) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}

		user.setPasswordHash(passwordEncoder.encode(user.getPassword())); // Mã hóa password
		return userRepository.save(user);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void updateUser(User user) {
		user.setUpdateAt(LocalDateTime.now());
		userRepository.save(user);
	}

	public boolean customerExists(Integer customerId) {
		return userRepository.existsById(customerId);
	}

	// Kiểm tra email đã tồn tại
	public boolean emailExists(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	// Lưu user tạm thời
	public void cachePendingUser(User user) {
		pendingUsers.put(user.getEmail(), user);
	}

	public User getPendingUser(String email) {
		return pendingUsers.get(email);
	}

	public void removePendingUser(String email) {
		pendingUsers.remove(email);
	}

	// Lưu vào DB sau khi xác thực
	public User saveFinalUser(User user) {
		return userRepository.save(user);
	}

	public boolean pendingUserExists(String email) {
		return pendingUsers.containsKey(email);
	}
}
