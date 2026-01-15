package com.example.xoso.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
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

  @Column(nullable = false)
  private LocalDate drawDate;

  @Column(nullable = false)
  private String prizeLevel;

  @Column(columnDefinition = "JSONB")
  private String numbers; // lưu JSON string

  private String sourceUrl;

  @Builder.Default
  private Instant fetchedAt = Instant.now();
}
