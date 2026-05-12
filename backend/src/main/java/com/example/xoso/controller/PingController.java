package com.example.xoso.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight endpoint dùng cho warm-up service (cron-job.org).
 * Được gọi lúc 15:50 ICT mỗi ngày để đánh thức server trước khi CrawlScheduler chạy.
 */
@RestController
public class PingController {

    @GetMapping("/api/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
