# Automated Dead Letter Queue Triage Pipeline

This repository contains a fault-tolerant system designed to automatically analyze, diagnose, and categorize messaging failures within an event-driven architecture. By combining high-throughput local heuristics with a local large language model fallback, the system processes failed messages from a Dead Letter Queue (DLQ), identifies the root cause of the exception, and logs a structured diagnostic report to a database for developers to review.

## System Architecture

The application is built using Spring Boot 3.3 and operates via a Chain of Responsibility pattern to balance processing speed with deep analytical fallback.

### Verification and Testing

using postman trigger this :
```bash
curl -X POST http://localhost:8080/api/test/simulate-dlq \
-H "Content-Type: application/json" \
-d '{
  "payload": "{\"transactionId\": \"TX-9981\", \"metadata\": \"This should be an object, not a string\"}",
  "stackTrace": "com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value..."
}'
```


### Component Flow

```mermaid
graph TD
    subgraph Upstream ["Upstream Environment"]
        Primary["Primary Event"]
    end

    subgraph Messaging ["Message Broker"]
        Kafka[ Kafka <br> "(dlq topic)"]
    end

    subgraph App ["          "]
        Consumer["Kafka DLQ Consumer"]
        Orchestrator["Triage Orchestrator"]
        Heuristics["Heuristics Engine <br> (Java Fast-Path)"]
        SpringAI["Spring AI Client"]
    end

    subgraph AI ["Local Inference Engine"]
        Ollama["Ollama API"]
        Llama3["Llama 3 Model"]
    end

    subgraph Storage ["Database Layer"]
        Postgres[("PostgreSQL Database")]
    end

    Primary --> Kafka
    Kafka --> Consumer
    Consumer --> Orchestrator
    
    Orchestrator --> Heuristics
    Orchestrator --> SpringAI
    
    SpringAI --> Ollama
    Ollama --> Llama3
    
    Orchestrator --> Postgres