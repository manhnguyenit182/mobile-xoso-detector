package com.example.xoso.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "draw_result", indexes = {
    @Index(name = "ix_draw_province_date", columnList = "provinceCode, drawDate"),
    @Index(name = "ix_draw_region_date", columnList = "region, drawDate")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrawResult {
  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String provinceCode;

  /**
   * Miền: MN = Miền Nam, MT = Miền Trung, MB = Miền Bắc
   */
  @Column(length = 2)
  @Builder.Default
  private String region = "MN";

  @Column(nullable = false)
  private LocalDate drawDate;

  @Column(nullable = false)
  private String prizeLevel;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String numbers; // lưu danh sách số phân cách bằng dấu phẩy

  private String sourceUrl;

  @Builder.Default
  private Instant fetchedAt = Instant.now();
}

