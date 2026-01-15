package com.example.xoso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.xoso.model.TicketScan;

import java.util.UUID;

@Repository
public interface TicketScanRepository extends JpaRepository<TicketScan, UUID> {
}
