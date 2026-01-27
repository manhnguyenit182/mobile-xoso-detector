package com.example.xoso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.xoso.dto.request.TicketRequest;
import com.example.xoso.service.DrawService;

@RestController()
public class checkTicketController {
  @Autowired
  private DrawService drawService;

  @PostMapping("/api/check-ticket")
  public Object checkTicket(@RequestBody TicketRequest request) {
    return drawService.checkTicket(request.getProvinceCode(), request.getDrawDate(), request.getTicketNumber());
  }
}
