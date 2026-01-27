package com.example.xoso.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

  @NotNull
  private UUID userId;

  @NotNull
  @Size(min = 6, max = 6)
  private String ticketNumber;

  @NotNull
  private String provinceCode;

  @NotNull
  private LocalDate drawDate;
}
