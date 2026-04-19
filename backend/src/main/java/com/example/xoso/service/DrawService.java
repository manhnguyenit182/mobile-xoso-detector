package com.example.xoso.service;

import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.xoso.repository.DrawResultRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service dò vé số hỗ trợ cả 3 miền:
 * - Miền Nam / Miền Trung: vé 6 số
 * - Miền Bắc: vé 5 số, cơ cấu giải thưởng khác
 *
 * Cơ cấu giải và số chữ số khớp đuôi:
 * Miền Nam/Trung:
 *   Giải ĐB (1): 6 số → 2 tỷ | KK (10): 5 số khác đầu → 40 triệu | KK2 (11): 5 số → 10 triệu
 *   Giải 1 (2): 5 số → 500 triệu | Giải 2 (3): 5 số → 300 triệu
 *   Giải 3 (4): 5 số → 100 triệu | Giải 4 (5): 5 số → 30 triệu
 *   Giải 5 (6): 4 số → 10 triệu  | Giải 6 (7): 4 số → 3 triệu
 *   Giải 7 (8): 3 số → 4 triệu   | Giải 8 (9): 2 số → 2 triệu
 *
 * Miền Bắc (vé 5 số, giải ĐB 5 số):
 *   Giải ĐB (1): 5 số → 2 tỷ
 *   Giải 1 (2): 5 số → 100 triệu | Giải 2 (3): 5 số x2 → 60 triệu
 *   Giải 3 (4): 5 số x6 → 30 triệu | Giải 4 (5): 4 số x4 → 6 triệu
 *   Giải 5 (6): 4 số → 3 triệu | Giải 6 (7): 3 số x3 → 1.5 triệu
 *   Giải 7 (8): 2 số x4 → 200 nghìn
 */
@Slf4j
@Service
public class DrawService {

  @Autowired
  private DrawResultRepository drawResultRepository;

  // Danh sách tỉnh/thành thuộc Miền Bắc
  private static final Set<String> MIEN_BAC_PROVINCES = new HashSet<>(Arrays.asList(
      "Hà Nội", "Hải Phòng", "Quảng Ninh", "Bắc Ninh", "Bắc Giang",
      "Hải Dương", "Hưng Yên", "Vĩnh Phúc", "Hà Nam", "Nam Định",
      "Thái Bình", "Ninh Bình", "Thái Nguyên", "Tuyên Quang", "Lào Cai",
      "Yên Bái", "Phú Thọ", "Điện Biên", "Hòa Bình", "Sơn La",
      "Lai Châu", "Lạng Sơn", "Cao Bằng", "Bắc Kạn", "Hà Giang"
  ));

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

    boolean isMienBac = MIEN_BAC_PROVINCES.contains(provinceCode);

    if (isMienBac) {
      checkMienBac(provinceCode, drawDate, ticketNumber, results);
    } else {
      checkMienNamTrung(provinceCode, drawDate, ticketNumber, results);
    }

    return results;
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // MIỀN NAM / MIỀN TRUNG (vé 6 số)
  // ─────────────────────────────────────────────────────────────────────────────

  private void checkMienNamTrung(String provinceCode, LocalDate drawDate, String ticketNumber,
      List<Map<String, Object>> results) {

    for (int i = 1; i <= 9; i++) {
      String prizeLevel = Integer.toString(i);

      if (prizeLevel.equals("1")) {
        // Giải ĐB: 6 số khớp = ĐB (2 tỷ); 5 số khớp (khác đầu) = KK1 (40 tr); 5 số = KK2 (10 tr)
        String specialPrize = drawResultRepository.findNumbersByProvinceCodeAndDrawDateAndPrizeLevel(
            provinceCode, drawDate, prizeLevel);
        if (specialPrize != null) {
          String prize6 = specialPrize.trim().replace(",", "").substring(0, Math.min(6, specialPrize.trim().replace(",", "").length()));
          int matchCount = countSuffixMatch(ticketNumber, prize6, prize6.length());
          log.debug("Giải ĐB: {} khớp {} số với {}", ticketNumber, matchCount, prize6);

          if (matchCount == 6) {
            addResult(provinceCode, drawDate, "ĐẶC BIỆT", ticketNumber, results, 2_000_000_000L);
          } else if (matchCount == 5 && ticketNumber.charAt(0) != prize6.charAt(0)) {
            addResult(provinceCode, drawDate, "KHUYẾN KHÍCH 1", ticketNumber, results, 40_000_000L);
          } else if (matchCount == 5) {
            addResult(provinceCode, drawDate, "KHUYẾN KHÍCH 2", ticketNumber, results, 10_000_000L);
          }
        }

      } else if (prizeLevel.equals("2") || prizeLevel.equals("3") || prizeLevel.equals("4") || prizeLevel.equals("5")) {
        // Giải 1–4: so 5 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 5);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) {
          long amount = switch (prizeLevel) {
            case "2" -> 500_000_000L;
            case "3" -> 300_000_000L;
            case "4" -> 100_000_000L;
            case "5" -> 30_000_000L;
            default -> 0L;
          };
          addResult(provinceCode, drawDate, "GIẢI " + getGradeLabel(prizeLevel), ticketNumber, results, amount);
        }

      } else if (prizeLevel.equals("6") || prizeLevel.equals("7")) {
        // Giải 5–6: so 4 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 4);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) {
          long amount = prizeLevel.equals("6") ? 10_000_000L : 3_000_000L;
          addResult(provinceCode, drawDate, "GIẢI " + getGradeLabel(prizeLevel), ticketNumber, results, amount);
        }

      } else if (prizeLevel.equals("8")) {
        // Giải 7: so 3 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 3);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) {
          addResult(provinceCode, drawDate, "GIẢI BẢY", ticketNumber, results, 4_000_000L);
        }

      } else if (prizeLevel.equals("9")) {
        // Giải 8: so 2 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 2);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) {
          addResult(provinceCode, drawDate, "GIẢI TÁM", ticketNumber, results, 2_000_000L);
        }
      }
    }
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // MIỀN BẮC (vé 5 số)
  // ─────────────────────────────────────────────────────────────────────────────

  private void checkMienBac(String provinceCode, LocalDate drawDate, String ticketNumber,
      List<Map<String, Object>> results) {

    for (int i = 1; i <= 8; i++) {
      String prizeLevel = Integer.toString(i);

      if (prizeLevel.equals("1")) {
        // Giải ĐB Miền Bắc: 5 số
        String specialPrize = drawResultRepository.findNumbersByProvinceCodeAndDrawDateAndPrizeLevel(
            provinceCode, drawDate, prizeLevel);
        if (specialPrize != null) {
          String prize5 = specialPrize.trim().replace(",", "").substring(0, Math.min(5, specialPrize.trim().replace(",", "").length()));
          int matchCount = countSuffixMatch(ticketNumber, prize5, prize5.length());
          if (matchCount == 5) {
            addResult(provinceCode, drawDate, "ĐẶC BIỆT", ticketNumber, results, 2_000_000_000L);
          }
        }

      } else if (prizeLevel.equals("2")) {
        // Giải 1: 5 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 5);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI NHẤT", ticketNumber, results, 100_000_000L);

      } else if (prizeLevel.equals("3")) {
        // Giải 2: 5 số đuôi (2 lần)
        String sub = ticketNumber.substring(ticketNumber.length() - 5);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI NHÌ", ticketNumber, results, 60_000_000L);

      } else if (prizeLevel.equals("4")) {
        // Giải 3: 5 số đuôi (6 lần)
        String sub = ticketNumber.substring(ticketNumber.length() - 5);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI BA", ticketNumber, results, 30_000_000L);

      } else if (prizeLevel.equals("5")) {
        // Giải 4: 4 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 4);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI TƯ", ticketNumber, results, 6_000_000L);

      } else if (prizeLevel.equals("6")) {
        // Giải 5: 4 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 4);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI NĂM", ticketNumber, results, 3_000_000L);

      } else if (prizeLevel.equals("7")) {
        // Giải 6: 3 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 3);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI SÁU", ticketNumber, results, 1_500_000L);

      } else if (prizeLevel.equals("8")) {
        // Giải 7: 2 số đuôi
        String sub = ticketNumber.substring(ticketNumber.length() - 2);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI BẢY", ticketNumber, results, 200_000L);
      }
    }
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // HELPERS
  // ─────────────────────────────────────────────────────────────────────────────

  /** Đếm số ký tự khớp từ đuôi giữa ticket và prize. */
  private int countSuffixMatch(String ticket, String prize, int maxLen) {
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

  private String getGradeLabel(String prizeLevel) {
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

  private void addResult(String provinceCode, LocalDate drawDate, String prizeLabel,
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
}
