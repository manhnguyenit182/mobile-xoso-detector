package com.example.xoso.dto.request;

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

  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  private String orcText;

  @NotNull
  private String provinceCode;

  @NotNull
  private String drawDate;
}
