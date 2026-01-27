package com.example.xoso.service;

import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.xoso.repository.DrawResultRepository;

@Service
public class DrawService {
  @Autowired
  private DrawResultRepository drawResultRepository;

  // đầu vào là ngày trên vé số và tỉnh,
  // lặp từ 1 đến 9 để kiểm tra từng giải có trùng với vé số hay không, nếu trúng
  // thì cho nó vào danh sách kết quả trả về
  public List<Map<String, Object>> checkTicket(String provinceCode, LocalDate drawDate, String ticketNumber) {
    List<Map<String, Object>> results = new ArrayList<>();
    System.out.println("Checking ticket: " + ticketNumber + " for province: " + provinceCode + " on date: " + drawDate);
    for (int i = 1; i <= 9; i++) {
      String prizeLevel = Integer.toString(i);
      // giải đặc biệt và khuyến khích
      if (prizeLevel.equals("1")) {
        String specialPrize = drawResultRepository.findNumbersByProvinceCodeAndDrawDateAndPrizeLevel(
            provinceCode, drawDate, prizeLevel);
        if (specialPrize != null) {
          int matchCount = 0;
          System.out.println("Special prize number: " + specialPrize);
          specialPrize = specialPrize.substring(0, 6);
          for (int j = 0; j < specialPrize.length(); j++) {
            if (ticketNumber.charAt(ticketNumber.length() - 1 - j) == specialPrize
                .charAt(specialPrize.length() - 1 - j)) {
              matchCount++;
            }
          }
          // 6 số bằng 6 thì đặc biệt, 5 sô và 2 số đầu khác thì dac biet khuyen khich, 5
          // số còn lại khuyen khich
          System.out.println("Match count for special prize: " + matchCount);
          if (matchCount == 6) {
            System.out.println("Found matching ticket in special prize");
            addExits(provinceCode, drawDate, prizeLevel, ticketNumber, results, 2000000000);
          } else if (matchCount == 5 && ticketNumber.charAt(0) != specialPrize.charAt(0)) {
            System.out.println("Found matching ticket in consolation prize");
            addExits(provinceCode, drawDate, "10", ticketNumber, results, 40000000);
          } else if (matchCount == 5) {
            System.out.println("Found matching ticket in consolation prize");
            addExits(provinceCode, drawDate, "11", ticketNumber, results, 10000000);
          }
        }
        // so sánh từ giải 2 đến giải 5, bỏ số đầu tiên
      } else if (prizeLevel.equals("2") || prizeLevel.equals("3") || prizeLevel.equals("4") || prizeLevel.equals("5")) {
        String subTicketNumber = ticketNumber.substring(1);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, subTicketNumber);
        if (exists) {
          System.out.println("Found matching ticket in prize level " + prizeLevel);
          addExits(provinceCode, drawDate, prizeLevel, ticketNumber, results, 500000000);
        }
        // so sánh từ giải 5 đến giải 6, bỏ 2 số đầu tiên
      } else if (prizeLevel.equals("6") || prizeLevel.equals("7")) {
        String subTicketNumber = ticketNumber.substring(2);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, subTicketNumber);
        if (exists) {
          System.out.println("Found matching ticket in prize level " + prizeLevel);
          addExits(provinceCode, drawDate, prizeLevel, ticketNumber, results, 10000000);
        }
      } else if (prizeLevel.equals("8")) {
        String subTicketNumber = ticketNumber.substring(3);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, subTicketNumber);
        if (exists) {
          System.out.println("Found matching ticket in prize level " + prizeLevel);
          addExits(provinceCode, drawDate, prizeLevel, ticketNumber, results, 4000000);
        }
      } else if (prizeLevel.equals("9")) {
        String subTicketNumber = ticketNumber.substring(4);
        boolean exists = drawResultRepository.existsByProvinceCodeAndDrawDateAndPrizeLevelAndNumbersContaining(
            provinceCode, drawDate, prizeLevel, subTicketNumber);
        if (exists) {
          System.out.println("Found matching ticket in prize level " + prizeLevel);
          addExits(provinceCode, drawDate, prizeLevel, ticketNumber, results, 2000000);
        }
      }
    }
    return results;

  }

  public void addExits(String provinceCode, LocalDate drawDate, String prizeLevel, String ticketNumber,
      List<Map<String, Object>> results, int prizeAmount) {
    Map<String, Object> result = new HashMap<>();
    result.put("provinceCode", provinceCode);
    result.put("drawDate", drawDate.toString());
    result.put("prizeLevel", prizeLevel);
    result.put("ticketNumber", ticketNumber);
    result.put("prizeAmount", prizeAmount);
    results.add(result);
  }
}
