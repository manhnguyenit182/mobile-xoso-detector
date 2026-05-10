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
 * - Miền Bắc: vé 5 số + ký hiệu
 *
 * Cơ cấu giải thưởng thực tế hiện hành:
 *
 * Miền Nam / Miền Trung (vé 6 số):
 *   Giải ĐB  (prizeLevel=1): khớp 6 số đuôi           → 2.000.000.000đ
 *   Giải Phụ ĐB             : khớp 5 số đuôi (sai hàng trăm nghìn) → 50.000.000đ
 *   Giải KK                 : sai đúng 1 số bất kỳ (không phải hàng trăm nghìn) → 6.000.000đ
 *   Giải Nhất (prizeLevel=2): khớp 5 số đuôi           → 30.000.000đ
 *   Giải Nhì  (prizeLevel=3): khớp 5 số đuôi           → 15.000.000đ
 *   Giải Ba   (prizeLevel=4): khớp 5 số đuôi           → 10.000.000đ
 *   Giải Tư   (prizeLevel=5): khớp 5 số đuôi           → 3.000.000đ
 *   Giải Năm  (prizeLevel=6): khớp 4 số đuôi           → 1.000.000đ
 *   Giải Sáu  (prizeLevel=7): khớp 4 số đuôi           → 400.000đ
 *   Giải Bảy  (prizeLevel=8): khớp 3 số đuôi           → 200.000đ
 *   Giải Tám  (prizeLevel=9): khớp 2 số đuôi           → 100.000đ
 *
 * Miền Bắc (vé 5 số + ký hiệu):
 *   Giải ĐB  (prizeLevel=1): khớp 5 số + ký hiệu       → 500.000.000đ (mỗi vé)
 *   Giải Phụ ĐB             : khớp đúng 5 số (khác ký hiệu) → 25.000.000đ
 *   Giải Nhất (prizeLevel=2): khớp 5 số đuôi           → 10.000.000đ
 *   Giải Nhì  (prizeLevel=3): khớp 5 số đuôi (2 dãy)   → 5.000.000đ
 *   Giải Ba   (prizeLevel=4): khớp 5 số đuôi (6 dãy)   → 1.000.000đ
 *   Giải Tư   (prizeLevel=5): khớp 4 số đuôi (4 dãy)   → 400.000đ
 *   Giải Năm  (prizeLevel=6): khớp 4 số đuôi           → 200.000đ
 *   Giải Sáu  (prizeLevel=7): khớp 3 số đuôi (3 dãy)   → 100.000đ
 *   Giải Bảy  (prizeLevel=8): khớp 2 số đuôi (4 dãy)   → 40.000đ
 *   Giải KK                 : khớp 2 số đuôi của ĐB     → 40.000đ
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
        // ── Giải Đặc Biệt ──────────────────────────────────────────────────────
        // - Khớp 6 số cuối  → ĐẶC BIỆT          (2.000.000.000đ)
        // - Khớp 5 số cuối (sai hàng trăm nghìn) → PHỤ ĐẶC BIỆT (50.000.000đ)
        // - Sai đúng 1 số bất kỳ (không phải trăm nghìn) → KHUYẾN KHÍCH (6.000.000đ)
        String specialPrize = drawResultRepository.findNumbersByProvinceCodeAndDrawDateAndPrizeLevel(
            provinceCode, drawDate, prizeLevel);
        if (specialPrize != null) {
          String rawPrize = specialPrize.trim().replace(",", "");
          String prize6 = rawPrize.substring(0, Math.min(6, rawPrize.length()));
          int matchCount = countSuffixMatch(ticketNumber, prize6, prize6.length());
          log.debug("Giải ĐB: {} khớp {} số với {}", ticketNumber, matchCount, prize6);

          if (matchCount == 6) {
            // Trúng Đặc Biệt
            addResult(provinceCode, drawDate, "ĐẶC BIỆT", ticketNumber, results, 2_000_000_000L);
          } else if (matchCount == 5 && ticketNumber.charAt(ticketNumber.length() - 6) != prize6.charAt(0)) {
            // Sai chữ số hàng trăm nghìn → Giải Phụ Đặc Biệt
            addResult(provinceCode, drawDate, "PHỤ ĐẶC BIỆT", ticketNumber, results, 50_000_000L);
          } else {
            // Kiểm tra Giải Khuyến Khích: sai đúng 1 số bất kỳ (không phải trăm nghìn)
            int diffCount = countDiffPositions(ticketNumber, prize6);
            if (diffCount == 1 && !isMismatchAtHundredThousand(ticketNumber, prize6)) {
              addResult(provinceCode, drawDate, "KHUYẾN KHÍCH", ticketNumber, results, 6_000_000L);
            }
          }
        }

      } else if (prizeLevel.equals("2") || prizeLevel.equals("3")
          || prizeLevel.equals("4") || prizeLevel.equals("5")) {
        // ── Giải Nhất → Tư: so 5 số đuôi ──────────────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(5, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) {
          long amount = switch (prizeLevel) {
            case "2" -> 30_000_000L;   // Giải Nhất
            case "3" -> 15_000_000L;   // Giải Nhì
            case "4" -> 10_000_000L;   // Giải Ba
            case "5" -> 3_000_000L;    // Giải Tư
            default -> 0L;
          };
          addResult(provinceCode, drawDate, "GIẢI " + getGradeLabel(prizeLevel), ticketNumber, results, amount);
        }

      } else if (prizeLevel.equals("6") || prizeLevel.equals("7")) {
        // ── Giải Năm → Sáu: so 4 số đuôi ──────────────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(4, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) {
          long amount = prizeLevel.equals("6") ? 1_000_000L : 400_000L;
          addResult(provinceCode, drawDate, "GIẢI " + getGradeLabel(prizeLevel), ticketNumber, results, amount);
        }

      } else if (prizeLevel.equals("8")) {
        // ── Giải Bảy: so 3 số đuôi ─────────────────────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(3, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) {
          addResult(provinceCode, drawDate, "GIẢI BẢY", ticketNumber, results, 200_000L);
        }

      } else if (prizeLevel.equals("9")) {
        // ── Giải Tám: so 2 số đuôi ─────────────────────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(2, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) {
          addResult(provinceCode, drawDate, "GIẢI TÁM", ticketNumber, results, 100_000L);
        }
      }
    }
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // MIỀN BẮC (vé 5 số + ký hiệu)
  // ─────────────────────────────────────────────────────────────────────────────

  private void checkMienBac(String provinceCode, LocalDate drawDate, String ticketNumber,
      List<Map<String, Object>> results) {

    for (int i = 1; i <= 8; i++) {
      String prizeLevel = Integer.toString(i);

      if (prizeLevel.equals("1")) {
        // ── Giải Đặc Biệt Miền Bắc ─────────────────────────────────────────────
        // - Khớp 5 số + đúng ký hiệu → ĐẶC BIỆT        (500.000.000đ/vé)
        // - Khớp đúng 5 số (sai ký hiệu) → PHỤ ĐẶC BIỆT (25.000.000đ)
        String specialPrize = drawResultRepository.findNumbersByProvinceCodeAndDrawDateAndPrizeLevel(
            provinceCode, drawDate, prizeLevel);
        if (specialPrize != null) {
          String rawPrize = specialPrize.trim().replace(",", "");
          String prize5 = rawPrize.substring(0, Math.min(5, rawPrize.length()));
          int matchCount = countSuffixMatch(ticketNumber, prize5, prize5.length());
          log.debug("Giải ĐB Miền Bắc: {} khớp {} số với {}", ticketNumber, matchCount, prize5);

          if (matchCount == 5) {
            // Khớp 5 số → ĐẶC BIỆT (mỗi vé trúng 500 triệu)
            addResult(provinceCode, drawDate, "ĐẶC BIỆT", ticketNumber, results, 500_000_000L);
          } else if (matchCount == 4) {
            // Khớp 4 số cuối (coi như sai ký hiệu/hàng trăm nghìn) → Phụ ĐB
            addResult(provinceCode, drawDate, "PHỤ ĐẶC BIỆT", ticketNumber, results, 25_000_000L);
          } else {
            // Kiểm tra Giải Khuyến Khích: 2 số cuối trùng với 2 số cuối của ĐB
            String ticket2 = ticketNumber.substring(ticketNumber.length() - Math.min(2, ticketNumber.length()));
            String prize2 = prize5.substring(prize5.length() - 2);
            if (ticket2.equals(prize2)) {
              addResult(provinceCode, drawDate, "KHUYẾN KHÍCH", ticketNumber, results, 40_000L);
            }
          }
        }

      } else if (prizeLevel.equals("2")) {
        // ── Giải Nhất: 5 số đuôi (10 triệu) ───────────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(5, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI NHẤT", ticketNumber, results, 10_000_000L);

      } else if (prizeLevel.equals("3")) {
        // ── Giải Nhì: 5 số đuôi, 2 dãy (5 triệu) ──────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(5, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI NHÌ", ticketNumber, results, 5_000_000L);

      } else if (prizeLevel.equals("4")) {
        // ── Giải Ba: 5 số đuôi, 6 dãy (1 triệu) ───────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(5, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI BA", ticketNumber, results, 1_000_000L);

      } else if (prizeLevel.equals("5")) {
        // ── Giải Tư: 4 số đuôi, 4 dãy (400 nghìn) ─────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(4, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI TƯ", ticketNumber, results, 400_000L);

      } else if (prizeLevel.equals("6")) {
        // ── Giải Năm: 4 số đuôi (200 nghìn) ───────────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(4, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI NĂM", ticketNumber, results, 200_000L);

      } else if (prizeLevel.equals("7")) {
        // ── Giải Sáu: 3 số đuôi, 3 dãy (100 nghìn) ────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(3, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI SÁU", ticketNumber, results, 100_000L);

      } else if (prizeLevel.equals("8")) {
        // ── Giải Bảy: 2 số đuôi, 4 dãy (40 nghìn) ─────────────────────────────
        String sub = ticketNumber.substring(ticketNumber.length() - Math.min(2, ticketNumber.length()));
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, sub);
        if (exists) addResult(provinceCode, drawDate, "GIẢI BẢY", ticketNumber, results, 40_000L);
      }
    }
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // HELPERS
  // ─────────────────────────────────────────────────────────────────────────────

  /**
   * Đếm số ký tự khớp liên tiếp từ đuôi (phải → trái) giữa ticket và prize.
   */
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

  /**
   * Đếm số vị trí khác nhau khi so sánh 6 số cuối của ticket với prize6.
   * Dùng để phát hiện Giải Khuyến Khích (sai đúng 1 số bất kỳ).
   */
  private int countDiffPositions(String ticket, String prize6) {
    if (ticket.length() < 6 || prize6.length() < 6) return 6;
    String t6 = ticket.substring(ticket.length() - 6);
    int diff = 0;
    for (int i = 0; i < 6; i++) {
      if (t6.charAt(i) != prize6.charAt(i)) diff++;
    }
    return diff;
  }

  /**
   * Kiểm tra xem vị trí sai duy nhất có phải hàng trăm nghìn (index 0) không.
   * Nếu sai hàng trăm nghìn thì là Giải Phụ ĐB, không phải KK.
   */
  private boolean isMismatchAtHundredThousand(String ticket, String prize6) {
    if (ticket.length() < 6 || prize6.length() < 6) return false;
    String t6 = ticket.substring(ticket.length() - 6);
    return t6.charAt(0) != prize6.charAt(0);
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
