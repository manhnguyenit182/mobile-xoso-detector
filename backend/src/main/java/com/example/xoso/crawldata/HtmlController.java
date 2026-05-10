package com.example.xoso.crawldata;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HtmlController {

  private final GetXoSo jsoupService;

  @GetMapping("/view-kqxs")
  public ResponseEntity<Object> viewKqxs() {
    try {
      java.util.Map<String, java.util.Map<String, java.util.List<String>>> jsondata = jsoupService.fetchData("https://www.kqxs.vn/mien-bac");
      if (jsondata == null) {
          return ResponseEntity.ok().body("Chưa có kết quả mới.");
      }
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE +
              ";charset=UTF-8")
          .body(jsondata);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Lỗi tải HTML: " + e.getMessage());
    }
  }
}
