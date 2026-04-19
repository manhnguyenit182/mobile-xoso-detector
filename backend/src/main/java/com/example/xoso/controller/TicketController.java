package com.example.xoso.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.xoso.dto.request.AnalyzeTicketRequest;
import com.example.xoso.dto.request.TicketRequest;
import com.example.xoso.dto.response.AnalyzeTicketResponse;
import com.example.xoso.service.DrawService;
import com.example.xoso.service.TicketAnalysisService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller xử lý các API liên quan đến vé số:
 * - POST /api/analyze-ticket : Nhận ảnh base64, OCR + AI parse + dò vé
 * - POST /api/check-ticket   : Dò vé thủ công (không cần ảnh)
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {

  private final TicketAnalysisService ticketAnalysisService;
  private final DrawService drawService;

  /**
   * Phân tích ảnh vé số: OCR → Gemini AI → Dò vé.
   * Thay thế logic gọi Google Vision và Gemini ở Frontend.
   */
  @PostMapping("/analyze-ticket")
  public ResponseEntity<AnalyzeTicketResponse> analyzeTicket(
      @Valid @RequestBody AnalyzeTicketRequest request) {
    log.info("Nhận yêu cầu phân tích ảnh vé số (base64 length: {})", request.getImageBase64().length());
    AnalyzeTicketResponse response = ticketAnalysisService.analyzeTicket(request.getImageBase64());
    return ResponseEntity.ok(response);
  }

  /**
   * Dò vé thủ công: Nhập tay số vé, đài, ngày.
   */
  @PostMapping("/check-ticket")
  public ResponseEntity<Object> checkTicket(@RequestBody TicketRequest request) {
    log.info("Dò vé thủ công: {} - {} - {}", request.getTicketNumber(), request.getProvinceCode(), request.getDrawDate());
    Object result = drawService.checkTicket(request.getProvinceCode(), request.getDrawDate(), request.getTicketNumber());
    return ResponseEntity.ok(result);
  }
}
