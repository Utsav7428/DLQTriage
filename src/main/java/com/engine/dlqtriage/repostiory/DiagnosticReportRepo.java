package com.engine.dlqtriage.repostiory;


import com.engine.dlqtriage.entity.DiagnosticReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticReportRepo extends JpaRepository<DiagnosticReport, Long> {
}
