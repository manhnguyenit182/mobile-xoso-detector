package com.example.xoso.service.strategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.xoso.repository.DrawResultRepository;

@Component
public class MienNamTrungPrizeChecker extends BasePrizeChecker {

    public MienNamTrungPrizeChecker(DrawResultRepository drawResultRepository) {
        super(drawResultRepository);
    }

    @Override
    public boolean supports(String provinceCode) {
        // Mặc định, nếu không phải Miền Bắc thì xử lý theo Miền Nam / Miền Trung
        return true; 
    }

    @Override
    public void checkTicket(String provinceCode, LocalDate drawDate, String ticketNumber, List<Map<String, Object>> results) {
        for (int i = 1; i <= 9; i++) {
            String prizeLevel = Integer.toString(i);

            if (prizeLevel.equals("1")) {
                String specialPrize = drawResultRepository.findNumbersByProvinceCodeAndDrawDateAndPrizeLevel(
                    provinceCode, drawDate, prizeLevel);
                if (specialPrize != null) {
                    String rawPrize = specialPrize.trim().replace(",", "");
                    String prize6 = rawPrize.substring(0, Math.min(6, rawPrize.length()));
                    int matchCount = countSuffixMatch(ticketNumber, prize6, prize6.length());
                    log.debug("Giải ĐB: {} khớp {} số với {}", ticketNumber, matchCount, prize6);

                    if (matchCount == 6) {
                        addResult(provinceCode, drawDate, "ĐẶC BIỆT", ticketNumber, results, 2_000_000_000L);
                    } else if (matchCount == 5 && ticketNumber.charAt(ticketNumber.length() - 6) != prize6.charAt(0)) {
                        addResult(provinceCode, drawDate, "PHỤ ĐẶC BIỆT", ticketNumber, results, 50_000_000L);
                    } else {
                        int diffCount = countDiffPositions(ticketNumber, prize6);
                        if (diffCount == 1 && !isMismatchAtHundredThousand(ticketNumber, prize6)) {
                            addResult(provinceCode, drawDate, "KHUYẾN KHÍCH", ticketNumber, results, 6_000_000L);
                        }
                    }
                }
            } else if (prizeLevel.equals("2") || prizeLevel.equals("3") || prizeLevel.equals("4") || prizeLevel.equals("5")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(5, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) {
                    long amount = switch (prizeLevel) {
                        case "2" -> 30_000_000L;
                        case "3" -> 15_000_000L;
                        case "4" -> 10_000_000L;
                        case "5" -> 3_000_000L;
                        default -> 0L;
                    };
                    addResult(provinceCode, drawDate, "GIẢI " + getGradeLabel(prizeLevel), ticketNumber, results, amount);
                }
            } else if (prizeLevel.equals("6") || prizeLevel.equals("7")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(4, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) {
                    long amount = prizeLevel.equals("6") ? 1_000_000L : 400_000L;
                    addResult(provinceCode, drawDate, "GIẢI " + getGradeLabel(prizeLevel), ticketNumber, results, amount);
                }
            } else if (prizeLevel.equals("8")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(3, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) {
                    addResult(provinceCode, drawDate, "GIẢI BẢY", ticketNumber, results, 200_000L);
                }
            } else if (prizeLevel.equals("9")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(2, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) {
                    addResult(provinceCode, drawDate, "GIẢI TÁM", ticketNumber, results, 100_000L);
                }
            }
        }
    }

    private int countDiffPositions(String ticket, String prize6) {
        if (ticket.length() < 6 || prize6.length() < 6) return 6;
        String t6 = ticket.substring(ticket.length() - 6);
        int diff = 0;
        for (int i = 0; i < 6; i++) {
            if (t6.charAt(i) != prize6.charAt(i)) diff++;
        }
        return diff;
    }

    private boolean isMismatchAtHundredThousand(String ticket, String prize6) {
        if (ticket.length() < 6 || prize6.length() < 6) return false;
        String t6 = ticket.substring(ticket.length() - 6);
        return t6.charAt(0) != prize6.charAt(0);
    }
}
