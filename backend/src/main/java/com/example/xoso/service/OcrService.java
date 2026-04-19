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

  private static final String VISION_URL = "https://vision.googleapis.com/v1/images:annotate";
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
  public String extractText(String base64Image) throws Exception {
    String url = VISION_URL + "?key=" + googleVisionApiKey;

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

    String requestBody = mapper.writeValueAsString(root);

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .timeout(Duration.ofSeconds(15))
        .build();

    HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Google Vision API lỗi: HTTP " + response.statusCode() + " - " + response.body());
    }

    JsonNode responseJson = mapper.readTree(response.body());
    JsonNode textAnnotations = responseJson.path("responses").get(0).path("textAnnotations");

    if (textAnnotations == null || textAnnotations.isEmpty()) {
      throw new RuntimeException("Không phát hiện văn bản nào trong ảnh");
    }

    return textAnnotations.get(0).path("description").asText();
  }
}
