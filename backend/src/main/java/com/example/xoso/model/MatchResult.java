package com.example.xoso.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "match_result", uniqueConstraints = {
        @UniqueConstraint(name = "uk_match_result", columnNames = { "ticketScanId", "drawResultId" })
}, indexes = {
        @Index(name = "ix_match_ticket", columnList = "ticketScanId"),
        @Index(name = "ix_match_draw", columnList = "drawResultId")
})
public class MatchResult {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID ticketScanId;

    @Column(nullable = false)
    private UUID drawResultId;

    @Column(nullable = false)
    private String matchedLevel; // DB, G1, NONE...

    @Column(columnDefinition = "JSONB")
    private String matchedNumbers; // lưu JSON string

    private Double payoutAmount;

    @Builder.Default
    private Instant matchedAt = Instant.now();

}
