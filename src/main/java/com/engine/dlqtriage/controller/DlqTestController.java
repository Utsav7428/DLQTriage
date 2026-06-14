package com.engine.dlqtriage.controller;

import com.engine.dlqtriage.entity.DiagnosticReport;
import com.engine.dlqtriage.repostiory.DiagnosticReportRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DlqTestController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DiagnosticReportRepo reportRepo;

    @PostMapping("/simulate-dlq")
    public String simulateDlqEvent(@RequestBody TestPayloadRequest request) {
        log.info("Simulating DLQ Event injection via REST...");

        Message<String> message = MessageBuilder
                .withPayload(request.payload())
                .setHeader(KafkaHeaders.TOPIC, "payment-events-dlq")
                .setHeader("exception-trace", request.stackTrace())
                .build();

        kafkaTemplate.send(message);

        return "Event successfully published to 'payment-events-dlq' topic. Check your console logs for triage processing!";
    }

    @GetMapping("/reports")
    public List<DiagnosticReport> getSavedReports() {
        return reportRepo.findAll();
    }

    public record TestPayloadRequest(String payload, String stackTrace) {}
}
