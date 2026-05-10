package com.example.xoso.service.strategy;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.xoso.repository.DrawResultRepository;

@Component
public class MienBacPrizeChecker extends BasePrizeChecker {

    private static final Set<String> MIEN_BAC_PROVINCES = new HashSet<>(Arrays.asList(
        "Hà Nội", "Hải Phòng", "Quảng Ninh", "Bắc Ninh", "Bắc Giang",
        "Hải Dương", "Hưng Yên", "Vĩnh Phúc", "Hà Nam", "Nam Định",
        "Thái Bình", "Ninh Bình", "Thái Nguyên", "Tuyên Quang", "Lào Cai",
        "Yên Bái", "Phú Thọ", "Điện Biên", "Hòa Bình", "Sơn La",
        "Lai Châu", "Lạng Sơn", "Cao Bằng", "Bắc Kạn", "Hà Giang"
    ));

    public MienBacPrizeChecker(DrawResultRepository drawResultRepository) {
        super(drawResultRepository);
    }

    @Override
    public boolean supports(String provinceCode) {
        return MIEN_BAC_PROVINCES.contains(provinceCode);
    }

    @Override
    public void checkTicket(String provinceCode, LocalDate drawDate, String ticketNumber, List<Map<String, Object>> results) {
        for (int i = 1; i <= 8; i++) {
            String prizeLevel = Integer.toString(i);

            if (prizeLevel.equals("1")) {
                String specialPrize = drawResultRepository.findNumbersByProvinceCodeAndDrawDateAndPrizeLevel(
                    provinceCode, drawDate, prizeLevel);
                if (specialPrize != null) {
                    String rawPrize = specialPrize.trim().replace(",", "");
                    String prize5 = rawPrize.substring(0, Math.min(5, rawPrize.length()));
                    int matchCount = countSuffixMatch(ticketNumber, prize5, prize5.length());
                    log.debug("Giải ĐB Miền Bắc: {} khớp {} số với {}", ticketNumber, matchCount, prize5);

                    if (matchCount == 5) {
                        addResult(provinceCode, drawDate, "ĐẶC BIỆT", ticketNumber, results, 500_000_000L);
                    } else if (matchCount == 4) {
                        addResult(provinceCode, drawDate, "PHỤ ĐẶC BIỆT", ticketNumber, results, 25_000_000L);
                    } else {
                        String ticket2 = ticketNumber.substring(ticketNumber.length() - Math.min(2, ticketNumber.length()));
                        String prize2 = prize5.substring(prize5.length() - 2);
                        if (ticket2.equals(prize2)) {
                            addResult(provinceCode, drawDate, "KHUYẾN KHÍCH", ticketNumber, results, 40_000L);
                        }
                    }
                }
            } else if (prizeLevel.equals("2")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(5, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) addResult(provinceCode, drawDate, "GIẢI NHẤT", ticketNumber, results, 10_000_000L);
            } else if (prizeLevel.equals("3")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(5, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) addResult(provinceCode, drawDate, "GIẢI NHÌ", ticketNumber, results, 5_000_000L);
            } else if (prizeLevel.equals("4")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(5, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) addResult(provinceCode, drawDate, "GIẢI BA", ticketNumber, results, 1_000_000L);
            } else if (prizeLevel.equals("5")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(4, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) addResult(provinceCode, drawDate, "GIẢI TƯ", ticketNumber, results, 400_000L);
            } else if (prizeLevel.equals("6")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(4, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) addResult(provinceCode, drawDate, "GIẢI NĂM", ticketNumber, results, 200_000L);
            } else if (prizeLevel.equals("7")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(3, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) addResult(provinceCode, drawDate, "GIẢI SÁU", ticketNumber, results, 100_000L);
            } else if (prizeLevel.equals("8")) {
                String sub = ticketNumber.substring(ticketNumber.length() - Math.min(2, ticketNumber.length()));
                boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
                    provinceCode, drawDate, prizeLevel, sub);
                if (exists) addResult(provinceCode, drawDate, "GIẢI BẢY", ticketNumber, results, 40_000L);
            }
        }
    }
}
