package com.example.xoso.service;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.xoso.crawldata.GetXoSo;
import com.example.xoso.repository.DrawResultRepository;

import tools.jackson.databind.ObjectMapper;

@Service
public class CrawlScheduler {
  DrawResultRepository drawResultRepository;

  // Chạy từ 17:30 đến 18:59, cứ 10 phút một lần
  // @Scheduled(cron = "0 */10 17-18 * * *", zone = "Asia/Ho_Chi_Minh")
  // @Scheduled(fixedRate = 10000, zone = "Asia/Ho_Chi_Minh")
  public void crawldataAt17h30() {
    // Implement scheduling logic here
    System.out.println("Crawling data at 10:05 AM every day...");
    GetXoSo getXoSo = new GetXoSo();
    try {
      String jsonData = getXoSo.fetchData("https://www.kqxs.vn/mien-nam");
      // System.out.println("Fetched JSON content: " + jsonData);
      if (jsonData != null) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, String>> dataMap = mapper.readValue(jsonData, Map.class);
        System.out.println("Parsed data: " + dataMap);

        // Here you can add logic to save dataMap to the database using
        // drawResultRepository
        for (String province : dataMap.keySet()) {
          System.out.println("Province: " + province + ", Results: " + dataMap.get(province));
        }

      } else {
        System.out.println("No new data available.");
      }

    } catch (Exception e) {
      System.err.println("Error fetching JSON: " + e.getMessage());
    }
  }
}
