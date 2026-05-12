package com.example.xoso.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight endpoint dùng cho:
 * - Warm-up: UptimeRobot ping mỗi 5 phút để giữ server không ngủ (Render free tier)
 * - Debug: kiểm tra server time / timezone để verify cron chạy đúng giờ
 */
@RestController
@RequestMapping("/api")
public class PingController {

  private static final DateTimeFormatter FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

  /** UptimeRobot gọi endpoint này mỗi 5 phút để giữ server thức. */
  @GetMapping("/ping")
  public ResponseEntity<String> ping() {
    return ResponseEntity.ok("pong");
  }

  /**
   * Kiểm tra server time — dùng sau khi deploy để verify TZ=Asia/Ho_Chi_Minh
   * đã được set đúng. Quan trọng: nếu timezone sai, cron sẽ chạy sai giờ.
   */
  @GetMapping("/server-time")
  public ResponseEntity<Map<String, String>> serverTime() {
    ZonedDateTime nowVN = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    ZonedDateTime nowUTC = ZonedDateTime.now(ZoneId.of("UTC"));
    return ResponseEntity.ok(Map.of(
        "vietnam_time", nowVN.format(FMT),
        "utc_time", nowUTC.format(FMT),
        "timezone_env", System.getenv().getOrDefault("TZ", "NOT_SET")
    ));
  }
}
