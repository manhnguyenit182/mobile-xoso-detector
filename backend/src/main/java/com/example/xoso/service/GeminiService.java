package com.example.xoso.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.xoso.dto.response.TicketInfoResponse;

/**
 * Service gọi Gemini AI để phân tích text OCR và trích xuất thông tin vé số.
 * Chạy ở Backend để bảo mật API Key.
 */
@Service
public class GeminiService {

  @Value("${gemini.api.key}")
  private String geminiApiKey;

  private static final String GEMINI_URL =
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent";

  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .build();
  private final ObjectMapper mapper = new ObjectMapper();

  private static final String SYSTEM_PROMPT = """
      Bạn là một hệ thống trích xuất dữ liệu có cấu trúc từ văn bản OCR của vé số Việt Nam.

      NHIỆM VỤ DUY NHẤT:
      - Phân tích văn bản OCR đầu vào
      - Trả về DUY NHẤT một object JSON hợp lệ

      ĐỊNH DẠNG BẮT BUỘC:
      - Chỉ được trả về JSON thuần
      - Không được giải thích
      - Không được thêm markdown
      - Không được thêm text trước hoặc sau JSON

      SCHEMA DUY NHẤT ĐƯỢC PHÉP:
      {
        "so_ve": "string | null",
        "ngay_xo_so": "yyyy-MM-dd | null",
        "dai_xo_so": "string | null"
      }

      QUY TẮC TRÍCH XUẤT:
      - Chỉ trích xuất dữ liệu nếu xuất hiện RÕ RÀNG trong OCR
      - Không suy đoán
      - Nếu không tìm thấy → trả về null cho field đó
      - Tên đài phải được chuẩn hoá (ví dụ: "BÌNH THUẬN" → "Bình Thuận")
      - so_ve chỉ được chứa các chữ số

      Văn bản OCR:
      """;

  /**
   * Phân tích OCR text bằng Gemini AI và trả về thông tin vé số có cấu trúc.
   *
   * @param ocrText văn bản OCR thô từ Google Vision
   * @return TicketInfoResponse chứa số vé, ngày xổ số, đài xổ số
   */
  public TicketInfoResponse parseTicketInfo(String ocrText) throws Exception {
    ObjectNode root = mapper.createObjectNode();
    ArrayNode contents = mapper.createArrayNode();
    ObjectNode contentNode = mapper.createObjectNode();
    ArrayNode parts = mapper.createArrayNode();
    ObjectNode part = mapper.createObjectNode();
    part.put("text", SYSTEM_PROMPT + ocrText);
    parts.add(part);
    contentNode.set("parts", parts);
    contents.add(contentNode);
    root.set("contents", contents);

    // Cấu hình thinkingLevel để nhanh hơn
    ObjectNode genConfig = mapper.createObjectNode();
    ObjectNode thinkingConfig = mapper.createObjectNode();
    thinkingConfig.put("thinkingBudget", 0); // disable thinking để phản hồi nhanh
    genConfig.set("thinkingConfig", thinkingConfig);
    root.set("generationConfig", genConfig);

    String requestBody = mapper.writeValueAsString(root);

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(GEMINI_URL))
        .header("Content-Type", "application/json")
        .header("x-goog-api-key", geminiApiKey)
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .timeout(Duration.ofSeconds(20))
        .build();

    HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Gemini API lỗi: HTTP " + response.statusCode() + " - " + response.body());
    }

    JsonNode responseJson = mapper.readTree(response.body());
    String rawJson = responseJson
        .path("candidates").get(0)
        .path("content").path("parts").get(0)
        .path("text").asText();

    // Xóa markdown code block nếu có
    rawJson = rawJson.trim();
    if (rawJson.startsWith("```")) {
      rawJson = rawJson.replaceAll("^```[a-z]*\\n?", "").replaceAll("```$", "").trim();
    }

    return mapper.readValue(rawJson, TicketInfoResponse.class);
  }
}
