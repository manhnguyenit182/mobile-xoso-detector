package com.example.xoso.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

  @Column(columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<String> numbers; // lưu JSON string

  private String sourceUrl;

  @Builder.Default
  private Instant fetchedAt = Instant.now();
}
