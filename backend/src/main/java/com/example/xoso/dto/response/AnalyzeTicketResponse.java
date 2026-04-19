package com.example.xoso.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response trả về sau khi phân tích ảnh vé số:
 * - Thông tin nhận dạng từ OCR + AI
 * - Kết quả dò vé (danh sách giải trúng)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeTicketResponse {

  /** Số vé nhận dạng được */
  private String ticketNumber;

  /** Ngày xổ số (yyyy-MM-dd) */
  private String drawDate;

  /** Tên đài xổ số (normalized) */
  private String provinceCode;

  /** true nếu có kết quả xổ số của ngày đó trong DB */
  private boolean resultsAvailable;

  /**
   * Danh sách các giải trúng.
   * Mỗi phần tử gồm: prizeLevel, prizeAmount, matchedNumbers, ...
   */
  private List<Map<String, Object>> prizes;

  /** Thông báo lỗi khi OCR/AI không nhận dạng được */
  private String errorMessage;
}
