package com.engine.dlqtriage.heurestics;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@Order(1)
public class MissingTransactionIdHeuristic implements PayloadHeuristic {

    private final Pattern missingTxIdPattern = Pattern.compile("\"transactionId\"\\s*:\\s*(null|\"\")");

    @Override
    public Optional<String> analyze(String payload, String stackTrace) {
        if (stackTrace.contains("transactionId") &&
                (stackTrace.contains("NullPointerException") || stackTrace.contains("not-null property references a null or transient value"))) {

            if (missingTxIdPattern.matcher(payload).find() || !payload.contains("\"transactionId\"")) {
                return Optional.of("ROOT CAUSE: Missing or null 'transactionId' in payload. | SUGGESTION: Reject at API Gateway or request payload schema validation before routing.");
            }
        }
        return Optional.empty();
    }
}