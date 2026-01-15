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
  public ResponseEntity<String> viewKqxs() {
    try {
      String jsondata = jsoupService.fetchData("https://www.kqxs.vn/mien-nam");
      System.out.println("Fetched JSON data: " + jsondata.length() + " characters");
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE +
              ";charset=UTF-8")
          .body(jsondata);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("<h1>Lỗi tải HTML</h1><pre>" +
          e.getMessage() + "</pre>");
    }
  }
}
