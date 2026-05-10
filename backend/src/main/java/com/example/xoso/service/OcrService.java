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

/**
 * Service gọi Google Cloud Vision API để đọc text từ ảnh (OCR).
 * Chạy ở Backend để bảo mật API Key.
 */
@Service
public class OcrService {

  @Value("${google.vision.api.key}")
  private String googleVisionApiKey;

  @Value("${google.vision.api.url}")
  private String googleVisionApiUrl;

  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .build();
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * Nhận ảnh dạng base64 và trả về toàn bộ văn bản nhận dạng được.
   *
   * @param base64Image chuỗi base64 của ảnh (không kèm data:image/... prefix)
   * @return raw OCR text
   */
  public String extractText(String base64Image) {
    String url = googleVisionApiUrl + "?key=" + googleVisionApiKey;

    // Build request body
    ObjectNode root = mapper.createObjectNode();
    ArrayNode requests = mapper.createArrayNode();
    ObjectNode reqNode = mapper.createObjectNode();

    ObjectNode image = mapper.createObjectNode();
    image.put("content", base64Image);
    reqNode.set("image", image);

    ArrayNode features = mapper.createArrayNode();
    ObjectNode feature = mapper.createObjectNode();
    feature.put("type", "TEXT_DETECTION");
    feature.put("maxResults", 10);
    features.add(feature);
    reqNode.set("features", features);

    requests.add(reqNode);
    root.set("requests", requests);

    String requestBody;
    try {
      requestBody = mapper.writeValueAsString(root);
    } catch (Exception e) {
      throw new com.example.xoso.exception.TicketAnalysisException("Lỗi build request body cho Google Vision", e);
    }

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .timeout(Duration.ofSeconds(15))
        .build();

    HttpResponse<String> response;
    try {
      response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      throw new com.example.xoso.exception.CrawlDataException("Lỗi kết nối Google Vision API", e);
    }

    if (response.statusCode() != 200) {
      throw new com.example.xoso.exception.TicketAnalysisException("Google Vision API lỗi: HTTP " + response.statusCode() + " - " + response.body());
    }

    JsonNode responseJson;
    try {
      responseJson = mapper.readTree(response.body());
    } catch (Exception e) {
      throw new com.example.xoso.exception.TicketAnalysisException("Lỗi parse JSON từ Google Vision API", e);
    }

    JsonNode textAnnotations = responseJson.path("responses").get(0).path("textAnnotations");

    if (textAnnotations == null || textAnnotations.isEmpty()) {
      throw new com.example.xoso.exception.TicketAnalysisException("Không phát hiện văn bản nào trong ảnh");
    }

    return textAnnotations.get(0).path("description").asText();
  }
}
