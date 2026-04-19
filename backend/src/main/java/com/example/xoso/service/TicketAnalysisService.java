package com.example.xoso.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.xoso.dto.response.AnalyzeTicketResponse;
import com.example.xoso.dto.response.TicketInfoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates the full ticket analysis pipeline:
 * 1. OCR (Google Vision Backend)
 * 2. AI Parsing (Gemini Backend)
 * 3. Prize Checking (DrawService)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketAnalysisService {

  private final OcrService ocrService;
  private final GeminiService geminiService;
  private final DrawService drawService;

  /**
   * Phân tích ảnh vé số từ base64 và trả về kết quả dò vé đầy đủ.
   *
   * @param base64Image chuỗi base64 của ảnh
   * @return AnalyzeTicketResponse
   */
  public AnalyzeTicketResponse analyzeTicket(String base64Image) {
    try {
      // Bước 1: OCR bằng Google Vision
      log.info("Bắt đầu OCR ảnh vé số...");
      String ocrText = ocrService.extractText(base64Image);
      log.info("OCR hoàn tất: {} ký tự", ocrText.length());

      // Bước 2: Phân tích bằng Gemini AI
      log.info("Phân tích thông tin vé bằng Gemini AI...");
      TicketInfoResponse ticketInfo = geminiService.parseTicketInfo(ocrText);
      log.info("Gemini phân tích: số vé={}, ngày={}, đài={}",
          ticketInfo.getSoVe(), ticketInfo.getNgayXoSo(), ticketInfo.getDaiXoSo());

      // Kiểm tra đủ thông tin không
      if (ticketInfo.getSoVe() == null || ticketInfo.getNgayXoSo() == null || ticketInfo.getDaiXoSo() == null) {
        return AnalyzeTicketResponse.builder()
            .ticketNumber(ticketInfo.getSoVe())
            .drawDate(ticketInfo.getNgayXoSo())
            .provinceCode(ticketInfo.getDaiXoSo())
            .resultsAvailable(false)
            .errorMessage("Không nhận dạng đủ thông tin vé số. Vui lòng chụp lại ảnh rõ hơn.")
            .build();
      }

      // Bước 3: Dò vé
      LocalDate drawDate = LocalDate.parse(ticketInfo.getNgayXoSo());
      log.info("Dò vé: {} - {} - {}", ticketInfo.getSoVe(), ticketInfo.getDaiXoSo(), drawDate);
      List<Map<String, Object>> prizes = drawService.checkTicket(
          ticketInfo.getDaiXoSo(), drawDate, ticketInfo.getSoVe());

      return AnalyzeTicketResponse.builder()
          .ticketNumber(ticketInfo.getSoVe())
          .drawDate(ticketInfo.getNgayXoSo())
          .provinceCode(ticketInfo.getDaiXoSo())
          .resultsAvailable(true)
          .prizes(prizes)
          .build();

    } catch (Exception e) {
      log.error("Lỗi khi phân tích vé số: {}", e.getMessage(), e);
      return AnalyzeTicketResponse.builder()
          .resultsAvailable(false)
          .errorMessage("Lỗi hệ thống: " + e.getMessage())
          .build();
    }
  }
}
