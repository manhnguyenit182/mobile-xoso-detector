package com.example.xoso.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request gửi ảnh vé số (base64) lên để backend phân tích.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeTicketRequest {

  /**
   * Chuỗi base64 của ảnh vé số (không kèm data:image/... prefix).
   */
  @NotBlank(message = "imageBase64 không được để trống")
  private String imageBase64;
}
