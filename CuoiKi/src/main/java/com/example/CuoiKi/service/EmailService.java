package com.example.CuoiKi.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	// Gửi OTP
	public void sendOtpEmail(String to, String otpCode) {
		sendEmail(to, "Mã xác nhận quên mật khẩu", "Mã OTP của bạn là: " + otpCode + ". Mã này sẽ hết hạn sau 5 phút.");
	}

	// Gửi mật khẩu mới
	public void sendNewPasswordEmail(String to, String newPassword) {
		sendEmail(to, "Mật khẩu mới của bạn",
				"Mật khẩu mới của bạn là: " + newPassword + ". Vui lòng đăng nhập và đổi lại mật khẩu.");
	}

	// Hàm dùng chung để gửi email
	public void sendEmail(String to, String subject, String content) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, false);

			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
		}
	}
}
