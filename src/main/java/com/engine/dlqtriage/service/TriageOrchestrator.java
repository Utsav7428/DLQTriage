package com.engine.dlqtriage.service;

import com.engine.dlqtriage.entity.DiagnosticReport;
import com.engine.dlqtriage.heurestics.PayloadHeuristic;
import com.engine.dlqtriage.repostiory.DiagnosticReportRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TriageOrchestrator {

    private final LlmInferenceService llmInferenceService;
    private final DiagnosticReportRepo reportRepo;
    private final List<PayloadHeuristic> heuristics;

    public void analyzeAndSave(String payload, String stackTrace) {
        DiagnosticReport report = new DiagnosticReport();
        report.setOriginalPayload(payload);
        report.setStackTrace(stackTrace);
        report.setProcessedAt(LocalDateTime.now());

        boolean resolvedByHeuristic = false;


        String determinedCause = null;
        for (PayloadHeuristic heuristic : heuristics) {
            Optional<String> result = heuristic.analyze(payload, stackTrace);
            if (result.isPresent()) {
                determinedCause = result.get();
                report.setIdentifiedBy("HEURISTIC");
                break;
            }
        }

        if (determinedCause == null) {
            determinedCause = llmInferenceService.diagnose(payload, stackTrace);
            report.setIdentifiedBy("LLM");
        }
        report.setRootCause(determinedCause);

        if (!resolvedByHeuristic) {
            log.info("Heuristics failed. Delegating to LLM...");

            String llmAnalysis = llmInferenceService.diagnose(payload, stackTrace);

            report.setRootCause(llmAnalysis);
            report.setIdentifiedBy("LLM");
        }

        reportRepo.save(report);
        log.info("Diagnostic report saved to database with ID: {}", report.getId());
    }
}