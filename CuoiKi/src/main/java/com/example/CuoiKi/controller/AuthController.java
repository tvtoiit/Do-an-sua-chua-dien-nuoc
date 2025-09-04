package com.example.CuoiKi.controller;

import java.util.Optional;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.CuoiKi.dto.ChangePasswordRequest;
import com.example.CuoiKi.dto.ResetPasswordRequest;
import com.example.CuoiKi.model.User;
import com.example.CuoiKi.security.JwtUtil;
import com.example.CuoiKi.service.EmailService;
import com.example.CuoiKi.service.OtpService;
import com.example.CuoiKi.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final UserService userService;
	private final EmailService emailService;
	private final OtpService otpService;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager,
			UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, EmailService emailService,
			OtpService otpService) {
		this.userService = userService;
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.otpService = otpService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody User user) {
		if (user.getEmail() == null || user.getEmail().isBlank()) {
			return ResponseEntity.badRequest().body("Email là bắt buộc.");
		}

		// Kiểm tra email đã tồn tại trong DB chưa
		if (userService.emailExists(user.getEmail())) {
			return ResponseEntity.badRequest().body("Email đã được sử dụng.");
		}

		// Mã hóa mật khẩu
		user.setPasswordHash(passwordEncoder.encode(user.getPassword()));

		// Tạm lưu user vào map (không lưu DB ngay)
		userService.cachePendingUser(user); // Ta sẽ cài hàm này ở bước 3

		// Gửi OTP xác nhận
		String otp = otpService.generateOtp(user.getEmail());
		emailService.sendEmail(user.getEmail(), "Xác nhận đăng ký",
				"Mã xác nhận đăng ký của bạn là: " + otp + ". Mã này có hiệu lực trong 5 phút.");

		return ResponseEntity.ok("Đã gửi mã xác nhận đến email. Vui lòng xác thực để hoàn tất đăng ký.");
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
		if (!otpService.validateOtp(email, otp)) {
			return ResponseEntity.badRequest().body("Mã xác nhận không hợp lệ hoặc đã hết hạn.");
		}

		User user = userService.getPendingUser(email);
		if (user == null) {
			return ResponseEntity.badRequest().body("Không tìm thấy thông tin đăng ký tạm thời.");
		}

		// Lưu vào DB
		User saved = userService.saveFinalUser(user);
		userService.removePendingUser(email); // Xóa khỏi bộ nhớ

		return ResponseEntity.ok("Đăng ký thành công!");
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<String> resendOtp(@RequestParam String email) {
		if (!userService.pendingUserExists(email)) {
			return ResponseEntity.badRequest().body("Email chưa bắt đầu quá trình đăng ký.");
		}

		String otp = otpService.generateOtp(email);
		emailService.sendEmail(email, "Gửi lại mã xác nhận",
				"Mã OTP mới của bạn là: " + otp + ". Mã có hiệu lực trong 5 phút.");

		return ResponseEntity.ok("Đã gửi lại mã xác nhận.");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User loginRequest) {
		Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());

		if (userOpt.isEmpty()) {
			return ResponseEntity.status(404).body("User not found");
		}

		User user = userOpt.get();

		// Kiểm tra mật khẩu bằng PasswordEncoder
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
			return ResponseEntity.status(401).body("Invalid password");
		}

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
		String token = jwtUtil.generateToken(userDetails.getUsername(), user.getUserId(), user.getRole());
		return ResponseEntity.ok(token);
	}

	// Gửi OTP qua email
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam String email) {
		Optional<User> userOpt = userService.findByEmail(email);
		if (userOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Email không tồn tại!");
		}

		String otp = otpService.generateOtp(email);
		emailService.sendOtpEmail(email, otp);
		return ResponseEntity.ok("Mã OTP đã được gửi đến email của bạn.");
	}

	// Xác nhận OTP và đặt lại mật khẩu mới
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
		if (!otpService.validateOtp(request.email, request.otp)) {
			return ResponseEntity.badRequest().body("Mã OTP không hợp lệ hoặc đã hết hạn!");
		}

		Optional<User> userOpt = userService.findByEmail(request.email);
		if (userOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Email không tồn tại!");
		}

		User user = userOpt.get();
		String newPassword = generateRandomPassword();
		user.setPasswordHash(passwordEncoder.encode(newPassword));
		userService.updateUser(user);

		emailService.sendNewPasswordEmail(request.email, newPassword);
		return ResponseEntity.ok("Mật khẩu mới đã được gửi đến email của bạn.");
	}

	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
		Optional<User> userOpt = userService.findByEmail(request.email);
		if (userOpt.isEmpty()) {
			return ResponseEntity.badRequest().body("Email không tồn tại!");
		}

		User user = userOpt.get();

		if (!passwordEncoder.matches(request.currentPassword, user.getPasswordHash())) {
			return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
		}

		if (!isValidPassword(request.newPassword)) {
			return ResponseEntity.badRequest().body(
					"Mật khẩu mới phải có ít nhất 8 ký tự, gồm ít nhất một chữ hoa, một chữ số và một ký tự đặc biệt!");
		}

		if (!request.newPassword.equals(request.confirmPassword)) {
			return ResponseEntity.badRequest().body("Mật khẩu mới và xác nhận mật khẩu không khớp!");
		}

		user.setPasswordHash(passwordEncoder.encode(request.newPassword));
		userService.updateUser(user);

		return ResponseEntity.ok("Đổi mật khẩu thành công!");
	}

	private String generateRandomPassword() {
		int length = 8;
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}

	// Hàm kiểm tra độ mạnh của mật khẩu
	private boolean isValidPassword(String password) {
		return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
	}
}
