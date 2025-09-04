package com.example.CuoiKi.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new HashMap<>();
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000; // 5 phút

    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStorage.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + OTP_VALID_DURATION);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        if (!otpStorage.containsKey(email)) {
            return false;
        }
        long expiryTime = otpExpiry.get(email);
        if (System.currentTimeMillis() > expiryTime) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            return false; // OTP hết hạn
        }
        boolean isValid = otp.equals(otpStorage.get(email));
        if (isValid) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
        }
        return isValid;
    }
}
