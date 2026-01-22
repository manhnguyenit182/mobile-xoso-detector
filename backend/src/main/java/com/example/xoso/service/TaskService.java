package com.example.xoso.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.xoso.dto.request.TicketRequest;
import com.example.xoso.dto.response.TicketResponse;
import com.example.xoso.exception.ResourceNotFoundException;
import com.example.xoso.model.TicketScan;
import com.example.xoso.repository.TicketScanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskService {
  private final TicketScanRepository ticketScanRepository;

  public List<TicketResponse> getAllTasks() {
    log.info("Fetching all tasks");
    List<TicketScan> tickets = ticketScanRepository.findAll();
    List<TicketResponse> responses = new ArrayList<>();
    for (TicketScan ticket : tickets) {
      responses.add(convertToResponse(ticket));
    }
    return responses;
  }

  public List<TicketResponse> getTasksByStatus(Boolean completed) {
    log.info("Fetching tasks with completed status: {}", completed);
    // Placeholder implementation - adjust based on your actual TicketScan model
    return getAllTasks();
  }

  public List<TicketResponse> searchTasks(String keyword) {
    log.info("Searching tasks with keyword: {}", keyword);
    // Placeholder implementation - adjust based on your actual search requirements
    return getAllTasks();
  }

  public TicketResponse getTaskById(UUID id) {
    log.info("Fetching task with id: {}", id);
    TicketScan ticket = ticketScanRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
    return convertToResponse(ticket);
  }

  @Transactional
  public TicketResponse createTask(TicketRequest request) {
    log.info("Creating new ticket for user: {}", request.getUserId());

    TicketScan ticket = TicketScan.builder()
        .userId(request.getUserId())
        .ocrText(request.getOrcText())
        .provinceCode(request.getProvinceCode())
        .drawDate(java.time.LocalDate.parse(request.getDrawDate()))
        .recognizedNumber(0) // Will be set after OCR processing
        .status("PENDING")
        .build();

    TicketScan savedTicket = ticketScanRepository.save(ticket);
    log.info("Ticket created successfully with id: {}", savedTicket.getId());

    return convertToResponse(savedTicket);
  }

  @Transactional
  public TicketResponse updateTask(UUID id, TicketRequest request) {
    log.info("Updating ticket with id: {}", id);

    TicketScan existingTicket = ticketScanRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));

    if (request.getUserId() != null) {
      existingTicket.setUserId(request.getUserId());
    }
    if (request.getOrcText() != null) {
      existingTicket.setOcrText(request.getOrcText());
    }
    if (request.getProvinceCode() != null) {
      existingTicket.setProvinceCode(request.getProvinceCode());
    }
    if (request.getDrawDate() != null) {
      existingTicket.setDrawDate(java.time.LocalDate.parse(request.getDrawDate()));
    }
    existingTicket.setUpdatedAt(java.time.Instant.now());

    TicketScan updatedTicket = ticketScanRepository.save(existingTicket);
    log.info("Ticket updated successfully with id: {}", updatedTicket.getId());

    return convertToResponse(updatedTicket);
  }

  @Transactional
  public void deleteTask(UUID id) {
    log.info("Deleting ticket with id: {}", id);

    if (!ticketScanRepository.existsById(id)) {
      throw new ResourceNotFoundException("Ticket", "id", id);
    }

    ticketScanRepository.deleteById(id);
    log.info("Ticket deleted successfully with id: {}", id);
  }

  private TicketResponse convertToResponse(TicketScan ticket) {
    return TicketResponse.builder()
        .matchedNumbers(null) // Set matched numbers if available
        .payoutAmount(0.0) // Calculate payout if needed
        .matchedAt(ticket.getCreatedAt() != null
            ? LocalDateTime.ofInstant(ticket.getCreatedAt(), java.time.ZoneId.systemDefault())
            : null)
        .build();
  }
}
