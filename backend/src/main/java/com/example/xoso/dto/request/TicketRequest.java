package com.example.xoso.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request dò vé thủ công (nhập tay thông tin vé).
 * Hỗ trợ cả vé Miền Nam/Trung (6 số) và Miền Bắc (5 số).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

  /** Số vé: 5-6 chữ số (Miền Bắc 5 số, Miền Nam/Trung 6 số) */
  @NotBlank(message = "Số vé không được để trống")
  @Pattern(regexp = "^\\d{5,6}$", message = "Số vé phải là 5-6 chữ số")
  private String ticketNumber;

  /** Tên đài xổ số (normalized, ví dụ: "Hà Nội", "Hồ Chí Minh") */
  @NotBlank(message = "Đài xổ số không được để trống")
  private String provinceCode;

  /** Ngày xổ số */
  @NotNull(message = "Ngày xổ số không được để trống")
  private LocalDate drawDate;
}

