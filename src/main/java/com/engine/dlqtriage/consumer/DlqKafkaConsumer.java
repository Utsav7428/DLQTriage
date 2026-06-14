package com.engine.dlqtriage.consumer;

import com.engine.dlqtriage.service.TriageOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DlqKafkaConsumer {

    private final TriageOrchestrator triageOrchestrator;

    @KafkaListener(topics = "payment-events-dlq", groupId = "dlq-triage-group")
    public void consume(String payload,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(value = "exception-trace", required = false) String stackTrace) {

        log.info("Received DLQ message from topic: {}", topic);
        triageOrchestrator.analyzeAndSave(payload, stackTrace != null ? stackTrace : "No stack trace provided");
    }
}