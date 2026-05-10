package com.example.xoso.service.strategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PrizeCheckStrategy {
    /**
     * Dò vé theo chiến lược cụ thể
     */
    void checkTicket(String provinceCode, LocalDate drawDate, String ticketNumber, List<Map<String, Object>> results);
    
    /**
     * Kiểm tra xem chiến lược này có hỗ trợ đài xổ số tương ứng không
     */
    boolean supports(String provinceCode);
}
