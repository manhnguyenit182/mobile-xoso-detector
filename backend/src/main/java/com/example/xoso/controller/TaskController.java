package com.example.xoso.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.xoso.dto.request.TicketRequest;
import com.example.xoso.dto.response.TicketResponse;
import com.example.xoso.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
  private final TaskService taskService;

  @GetMapping
  public ResponseEntity<List<TicketResponse>> getTasks(
      @RequestParam(required = false) Boolean completed,
      @RequestParam(required = false) String search) {
    log.info("Getting tasks - completed: {}, search: {}", completed, search);

    List<TicketResponse> tasks;
    if (completed != null) {
      tasks = taskService.getTasksByStatus(completed);
    } else if (search != null && !search.trim().isEmpty()) {
      tasks = taskService.searchTasks(search);
    } else {
      tasks = taskService.getAllTasks();
    }

    return ResponseEntity.ok(tasks);
  }

  @PostMapping
  public ResponseEntity<TicketResponse> createTask(@Valid @RequestBody TicketRequest request) {
    log.info("Creating new task: {}", request);
    TicketResponse response = taskService.createTask(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TicketResponse> getTaskById(@PathVariable UUID id) {
    log.info("Getting task by id: {}", id);
    TicketResponse response = taskService.getTaskById(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TicketResponse> updateTask(
      @PathVariable UUID id,
      @Valid @RequestBody TicketRequest request) {
    log.info("Updating task id: {}", id);
    TicketResponse response = taskService.updateTask(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
    log.info("Deleting task id: {}", id);
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
