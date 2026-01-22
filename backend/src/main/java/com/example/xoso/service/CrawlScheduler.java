package com.example.xoso.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.xoso.crawldata.GetXoSo;
import com.example.xoso.model.DrawResult;
import com.example.xoso.repository.DrawResultRepository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class CrawlScheduler {
  @Autowired
  private DrawResultRepository drawResultRepository;

  private LocalDate lastSavedDate = null;

  // Chạy từ 17:30 đến 18:59, cứ 10 phút một lần
  @Scheduled(cron = "0 15/10 16-17 * * *", zone = "Asia/Ho_Chi_Minh")
  // @Scheduled(fixedRate = 10000, zone = "Asia/Ho_Chi_Minh")
  public void crawldataAt17h30() {
    LocalDate today = LocalDate.now();

    // Kiểm tra nếu đã lưu dữ liệu hôm nay rồi thì bỏ qua
    if (lastSavedDate != null && lastSavedDate.equals(today)) {
      System.out.println("Data for today has already been saved. Skipping...");
      return;
    }

    // Implement scheduling logic here
    System.out.println("Crawling data at 17:30-18:59...");
    GetXoSo getXoSo = new GetXoSo();
    try {
      String jsonData = getXoSo.fetchData("https://www.kqxs.vn/mien-nam");
      // System.out.println("Fetched JSON content: " + jsonData);
      if (jsonData != null) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, List<String>>> dataMap = mapper.readValue(jsonData,
            new TypeReference<Map<String, Map<String, List<String>>>>() {
            });

        List<DrawResult> drawResults = new ArrayList<>();
        Date drawDate = new Date();

        for (String province : dataMap.keySet()) {
          Map<String, List<String>> prizeLevels = dataMap.get(province);

          for (String prizeLevel : prizeLevels.keySet()) {
            List<String> numbers = prizeLevels.get(prizeLevel);

            // Lưu cả List numbers vào một record thay vì tách từng số
            DrawResult drawResult = new DrawResult();
            drawResult.setProvinceCode(province);
            drawResult.setPrizeLevel(prizeLevel);
            drawResult.setNumbers(numbers); // Lưu toàn bộ List
            drawResult.setDrawDate(new java.sql.Date(drawDate.getTime()).toLocalDate());

            drawResults.add(drawResult);
          }
        }

        // Lưu tất cả cùng lúc
        drawResultRepository.saveAll(drawResults);
        System.out.println("Saved " + drawResults.size() + " records to database.");

        lastSavedDate = today;
        System.out.println("Data saved successfully. Scheduler will skip until tomorrow.");
      } else {
        System.out.println("No new data available.");
      }

    } catch (Exception e) {
      System.err.println("Error fetching JSON: " + e.getMessage());
    }
  }
}
