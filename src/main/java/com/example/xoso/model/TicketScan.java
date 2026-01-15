package com.example.xoso.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_scan", indexes = {
    @Index(name = "ix_ticket_scan_lookup", columnList = "recognizedNumber, provinceCode, drawDate")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketScan {

  @Id
  @GeneratedValue
  private UUID id;

  private UUID userId;

  @Column(columnDefinition = "TEXT")
  private String ocrText;

  @Column(nullable = false)
  private int recognizedNumber;

  @Column(nullable = false)
  private String provinceCode;

  @Column(nullable = false)
  private LocalDate drawDate;

  @Column(nullable = false)
  private String status; // PENDING, MATCHED, NO_RESULT, ERROR

  @Builder.Default
  private Instant createdAt = Instant.now();

  @Builder.Default
  private Instant updatedAt = Instant.now();
}
