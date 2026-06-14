package com.engine.dlqtriage.heurestics;

import java.util.Optional;

public interface PayloadHeuristic {

    Optional<String> analyze(String payload, String stackTrace);
}