package com.example.xoso.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

  private Map<String, Object> matchedNumbers;
  private Double payoutAmount;
  private LocalDateTime matchedAt;
}
