package com.shadowledger.shadow_ledger_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CorrectionEventConsumer {

    @KafkaListener(topics = "transactions.corrections", groupId = "shadow-ledger")
    public void consume(ConsumerRecord<String, String> record) {

        String traceId = extractTraceId(record);
        if (traceId != null) {
            MDC.put("traceId", traceId);
        }

        try {
            log.info("Consumed correction event: {}", record.value());
            // existing correction logic
        } finally {
            MDC.clear();
        }
    }

    private String extractTraceId(ConsumerRecord<?, ?> record) {
        Header header = record.headers().lastHeader("X-Trace-Id");
        return header != null
                ? new String(header.value(), StandardCharsets.UTF_8)
                : null;
    }
}
