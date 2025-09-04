package com.example.CuoiKi.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CuoiKi.model.User;
import com.example.CuoiKi.repository.UserRepository;
import com.example.CuoiKi.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private final UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping
	public ResponseEntity<User> register(@RequestBody User user) {
		return ResponseEntity.ok(userService.registerUser(user));
	}

	@GetMapping
	public List<User> getAllUsers() {
		return userRepository.findByDeleteFlg(false);
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Integer id) {
		return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public User updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
		return userRepository.findById(id).map(user -> {
			user.setFullName(updatedUser.getFullName());
			user.setEmail(updatedUser.getEmail());
			user.setPhoneNumber(updatedUser.getPhoneNumber());
			user.setAddress(updatedUser.getAddress());
			user.setRole(updatedUser.getRole());
			user.setUpdateAt(LocalDateTime.now());
			// Chỉ mã hóa lại nếu password không rỗng
			if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
				user.setPasswordHash(passwordEncoder.encode(updatedUser.getPassword()));
			}
			return userRepository.save(user);
		}).orElseThrow(() -> new RuntimeException("User not found"));
	}

	// @DeleteMapping("/{id}")
	// public void deleteUser(@PathVariable Integer id) {
	// userRepository.deleteById(id);
	// }

	@PutMapping("/delete/{id}")
	public User deleteUser(@PathVariable Integer id) {
		return userRepository.findById(id).map(user -> {
			user.setDeleteAt(LocalDateTime.now());
			user.setDeleteFlg(true);
			return userRepository.save(user);
		}).orElseThrow(() -> new RuntimeException("User not found"));
	}
}
