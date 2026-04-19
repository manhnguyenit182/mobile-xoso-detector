package com.example.xoso.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.xoso.crawldata.GetXoSo;
import com.example.xoso.model.DrawResult;
import com.example.xoso.repository.DrawResultRepository;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Scheduler tự động crawl kết quả xổ số 3 miền:
 * - Miền Nam: 16h30–18h00 (cứ 5 phút)
 * - Miền Trung: 17h15–18h30 (cứ 5 phút)
 * - Miền Bắc: 18h10–19h00 (cứ 5 phút)
 */
@Slf4j
@Service
public class CrawlScheduler {

  @Autowired
  private DrawResultRepository drawResultRepository;

  @Value("${crawl.url.mien-nam}")
  private String urlMienNam;

  @Value("${crawl.url.mien-trung}")
  private String urlMienTrung;

  @Value("${crawl.url.mien-bac}")
  private String urlMienBac;

  // Lưu last saved per region để tránh lưu trùng
  private final Map<String, LocalDate> lastSavedDate = new HashMap<>();
  private final Map<String, Map<String, Map<String, List<String>>>> lastFetchedData = new HashMap<>();

  // ─────────────────────────────────────────────────────────────────────────────
  // MIỀN NAM: Quay lúc 16h30 (giờ Việt Nam) → crawl 16h–18h mỗi 5 phút
  // ─────────────────────────────────────────────────────────────────────────────
  @Scheduled(cron = "0 */5 16-17 * * *", zone = "Asia/Ho_Chi_Minh")
  public void crawlMienNam() {
    crawlRegion("MN", urlMienNam);
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // MIỀN TRUNG: Quay lúc 17h15 → crawl 17h–18h30 mỗi 5 phút
  // ─────────────────────────────────────────────────────────────────────────────
  @Scheduled(cron = "0 */5 17-18 * * *", zone = "Asia/Ho_Chi_Minh")
  public void crawlMienTrung() {
    crawlRegion("MT", urlMienTrung);
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // MIỀN BẮC: Quay lúc 18h10–18h30 → crawl 18h–19h mỗi 5 phút
  // ─────────────────────────────────────────────────────────────────────────────
  @Scheduled(cron = "0 */5 18-19 * * *", zone = "Asia/Ho_Chi_Minh")
  public void crawlMienBac() {
    crawlRegion("MB", urlMienBac);
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // CORE CRAWL LOGIC (shared)
  // ─────────────────────────────────────────────────────────────────────────────
  private void crawlRegion(String region, String url) {
    LocalDate today = LocalDate.now();

    if (today.equals(lastSavedDate.get(region))) {
      log.info("[{}] Dữ liệu hôm nay đã được lưu. Bỏ qua.", region);
      return;
    }

    log.info("[{}] Bắt đầu crawl dữ liệu từ: {}", region, url);
    GetXoSo getXoSo = new GetXoSo();
    try {
      String jsonData = getXoSo.fetchData(url);
      if (jsonData == null) {
        log.info("[{}] Dữ liệu chưa có.", region);
        return;
      }

      ObjectMapper mapper = new ObjectMapper();
      Map<String, Map<String, List<String>>> dataMap = mapper.readValue(jsonData,
          new TypeReference<Map<String, Map<String, List<String>>>>() {});

      Map<String, Map<String, List<String>>> cached = lastFetchedData.get(region);
      if (dataMap.equals(cached)) {
        log.info("[{}] Dữ liệu không đổi so với lần trước → Lưu vào DB.", region);
        saveToDb(region, today, dataMap, url);
        lastSavedDate.put(region, today);
        log.info("[{}] ✓ Đã lưu dữ liệu ngày {}.", region, today);
      } else {
        log.info("[{}] Dữ liệu mới vừa fetch, chờ lần crawl tiếp theo để xác nhận ổn định.", region);
        lastFetchedData.put(region, dataMap);
      }

    } catch (Exception e) {
      log.error("[{}] Lỗi khi crawl dữ liệu: {}", region, e.getMessage(), e);
    }
  }

  private void saveToDb(String region, LocalDate drawDate, Map<String, Map<String, List<String>>> dataMap, String url) {
    List<DrawResult> drawResults = new ArrayList<>();

    for (String province : dataMap.keySet()) {
      Map<String, List<String>> prizeLevels = dataMap.get(province);

      for (String prizeLevel : prizeLevels.keySet()) {
        List<String> numbers = prizeLevels.get(prizeLevel);
        String numbersString = String.join(",", numbers) + ",";

        DrawResult drawResult = DrawResult.builder()
            .provinceCode(province)
            .region(region)
            .prizeLevel(prizeLevel)
            .numbers(numbersString)
            .drawDate(drawDate)
            .sourceUrl(url)
            .build();

        drawResults.add(drawResult);
      }
    }

    drawResultRepository.saveAll(drawResults);
    log.info("[{}] ✓ Đã lưu {} records vào database.", region, drawResults.size());
  }
}
