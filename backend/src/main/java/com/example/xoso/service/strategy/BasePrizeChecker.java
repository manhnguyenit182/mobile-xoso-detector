package com.example.xoso.service.strategy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.xoso.repository.DrawResultRepository;

public abstract class BasePrizeChecker implements PrizeCheckStrategy {
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final DrawResultRepository drawResultRepository;

    protected BasePrizeChecker(DrawResultRepository drawResultRepository) {
        this.drawResultRepository = drawResultRepository;
    }

    protected void addResult(String provinceCode, LocalDate drawDate, String prizeLabel,
                           String ticketNumber, List<Map<String, Object>> results, long prizeAmount) {
        Map<String, Object> result = new HashMap<>();
        result.put("provinceCode", provinceCode);
        result.put("drawDate", drawDate.toString());
        result.put("prizeLevel", prizeLabel);
        result.put("ticketNumber", ticketNumber);
        result.put("prizeAmount", prizeAmount);
        results.add(result);
        log.info("✓ Trúng {} - {} đồng", prizeLabel, prizeAmount);
    }

    protected String getGradeLabel(String prizeLevel) {
        return switch (prizeLevel) {
            case "2" -> "NHẤT";
            case "3" -> "NHÌ";
            case "4" -> "BA";
            case "5" -> "TƯ";
            case "6" -> "NĂM";
            case "7" -> "SÁU";
            case "8" -> "BẢY";
            case "9" -> "TÁM";
            default -> prizeLevel;
        };
    }

    protected int countSuffixMatch(String ticket, String prize, int maxLen) {
        int count = 0;
        int tLen = ticket.length();
        int pLen = prize.length();
        for (int j = 0; j < maxLen; j++) {
            if (tLen - 1 - j >= 0 && pLen - 1 - j >= 0
                && ticket.charAt(tLen - 1 - j) == prize.charAt(pLen - 1 - j)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
