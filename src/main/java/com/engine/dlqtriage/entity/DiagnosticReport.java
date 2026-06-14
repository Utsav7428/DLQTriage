package com.engine.dlqtriage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostic_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalPayload;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    private String rootCause;
    private String resolutionSuggestion;

    @Column(nullable = false)
    private String identifiedBy;

    private LocalDateTime processedAt;
}

