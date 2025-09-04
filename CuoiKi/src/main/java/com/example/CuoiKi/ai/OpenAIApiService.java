package com.example.CuoiKi.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenAIApiService {

	private static final String API_KEY = "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";
	private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

	private final OkHttpClient httpClient = new OkHttpClient();

	public String moderateArticle(String title, String description, String content) throws IOException {
		JSONObject message = new JSONObject();
		message.put("role", "user");

		// 🔥 prompt có chèn dữ liệu đúng cách
		String prompt = String.format(
				"""
						Bạn là một hệ thống kiểm duyệt nội dung. Hãy kiểm tra nội dung sau đây có vi phạm các điều kiện sau hay không:

						1. Mang tính chất bạo lực, kích động, đe dọa.
						2. Mang tính chất tôn giáo cực đoan hoặc gây tranh cãi về tôn giáo.
						3. Vi phạm pháp luật hoặc cổ vũ hành vi trái pháp luật.
						4. Sử dụng ngôn từ nhạy cảm, thô tục, phân biệt đối xử hoặc mang tính xúc phạm.
						5. Không liên quan đến việc chia sẻ kinh nghiệm sửa chữa thiết bị gia dụng, hoặc không nói về các vấn đề của thiết bị gia dụng.

						Trả lời bằng JSON với định dạng:
						{"result": "Approved", "reason": ""}
						hoặc
						{"result": "Rejected", "reason": "Lý do từ chối bài viết"}

						Tiêu đề: %s

						Mô tả: %s

						Nội dung: %s
						""",
				title, description, content);

		message.put("content", prompt);

		JSONObject bodyJson = new JSONObject();
		bodyJson.put("model", "openai/gpt-3.5-turbo");
		bodyJson.put("messages", new JSONArray().put(message));
		bodyJson.put("temperature", 0.2);

		RequestBody requestBody = RequestBody.create(bodyJson.toString(), MediaType.parse("application/json"));

		Request request = new Request.Builder().url(API_URL).header("Authorization", "Bearer " + API_KEY)
				.header("HTTP-Referer", "http://localhost:9090").header("X-Title", "CuoiKi").post(requestBody).build();

		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException(
						"Lỗi khi gọi OpenRouter API: " + response.code() + " - " + response.body().string());
			}

			String responseBody = response.body().string();
			JSONObject json = new JSONObject(responseBody);
			String contentReply = json.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
					.getString("content");

			// ✅ Kiểm tra nội dung phản hồi bắt đầu bằng { để chắc chắn là JSON
			if (!contentReply.trim().startsWith("{")) {
				throw new IOException("Phản hồi không hợp lệ từ AI: " + contentReply);
			}

			return contentReply;
		}
	}

}
