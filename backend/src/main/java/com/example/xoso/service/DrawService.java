package com.example.xoso.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.xoso.service.strategy.PrizeCheckStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * Service dò vé số.
 * Sử dụng Strategy Pattern để tự động phân luồng logic dò cho Miền Bắc hoặc Miền Nam/Trung.
 */
@Slf4j
@Service
public class DrawService {

  private final List<PrizeCheckStrategy> prizeCheckStrategies;

  @Autowired
  public DrawService(List<PrizeCheckStrategy> prizeCheckStrategies) {
    this.prizeCheckStrategies = prizeCheckStrategies;
  }

  /**
   * Kiểm tra vé số. Tự động nhận diện miền để áp dụng logic phù hợp.
   */
  public List<Map<String, Object>> checkTicket(String provinceCode, LocalDate drawDate, String ticketNumber) {
    List<Map<String, Object>> results = new ArrayList<>();

    if (ticketNumber == null || ticketNumber.isBlank()) {
      log.warn("Số vé rỗng, bỏ qua dò vé.");
      return results;
    }

    log.info("Dò vé: {} | Đài: {} | Ngày: {}", ticketNumber, provinceCode, drawDate);

    // Tìm Strategy phù hợp
    PrizeCheckStrategy selectedStrategy = prizeCheckStrategies.stream()
        .filter(strategy -> strategy.supports(provinceCode))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chiến lược dò vé cho đài: " + provinceCode));

    selectedStrategy.checkTicket(provinceCode, drawDate, ticketNumber, results);

    return results;
  }
}
