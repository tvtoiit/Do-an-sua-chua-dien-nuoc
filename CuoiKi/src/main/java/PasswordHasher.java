import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class PasswordHasher {
	public static void main(String[] args) {
		String password = "123456"; // Mật khẩu cần băm
		System.out.println("Password Hash: " + hashPassword(password));
	}

	public static String hashPassword(String password) {
		try {
			// Khởi tạo đối tượng MessageDigest với thuật toán SHA-256
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// Tính toán băm từ mật khẩu
			byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

			// Chuyển đổi mảng byte thành chuỗi hex
			return bytesToHex(encodedHash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Hàm chuyển đổi mảng byte thành chuỗi hex
	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
}
