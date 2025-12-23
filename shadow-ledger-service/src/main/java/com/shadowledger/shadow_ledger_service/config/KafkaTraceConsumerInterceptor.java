package com.shadowledger.shadow_ledger_service.config;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;

import java.util.Map;

public class KafkaTraceConsumerInterceptor
        implements ConsumerInterceptor<String, Object> {

    @Override
    public ConsumerRecords<String, Object> onConsume(
            ConsumerRecords<String, Object> records) {

        records.forEach(record -> {
            Header traceHeader = record.headers().lastHeader("X-Trace-Id");
            if (traceHeader != null) {
                String traceId = new String(traceHeader.value());
                MDC.put("traceId", traceId);
            }
        });

        return records;
    }

    @Override public void onCommit(Map offsets) {}
    @Override public void close() {}
    @Override public void configure(Map<String, ?> configs) {}
}
