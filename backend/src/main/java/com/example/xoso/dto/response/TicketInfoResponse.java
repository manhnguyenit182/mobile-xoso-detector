package com.example.xoso.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận kết quả phân tích vé số từ Gemini AI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketInfoResponse {

  @JsonProperty("so_ve")
  private String soVe;

  @JsonProperty("ngay_xo_so")
  private String ngayXoSo;

  @JsonProperty("dai_xo_so")
  private String daiXoSo;
}
