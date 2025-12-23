package com.shadowledger.shadow_ledger_service.consumer;

import com.shadowledger.shadow_ledger_service.dto.EventDto;
import com.shadowledger.shadow_ledger_service.model.EventType;
import com.shadowledger.shadow_ledger_service.model.LedgerEvent;
import com.shadowledger.shadow_ledger_service.repository.LedgerEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class EventConsumer {

    private final LedgerEventRepository repository;

    public EventConsumer(LedgerEventRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = {"transactions.raw", "transactions.corrections"},
            groupId = "shadow-ledger-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, EventDto> record) {

        try {
            // 🔑 Restore traceId from Kafka header
            Header traceHeader = record.headers().lastHeader("X-Trace-Id");
            if (traceHeader != null) {
                MDC.put("traceId",
                        new String(traceHeader.value(), StandardCharsets.UTF_8));
            }

            EventDto dto = record.value();
            if (dto == null) return;

            // Deduplication
            if (repository.findByEventId(dto.eventId()).isPresent()) {
                log.warn("Skipping duplicate event {}", dto.eventId());
                return;
            }

            LedgerEvent event = new LedgerEvent();
            event.setEventId(dto.eventId());
            event.setAccountId(dto.accountId());

            try {
                event.setType(EventType.valueOf(dto.type().toUpperCase()));
            } catch (IllegalArgumentException | NullPointerException e) {
                log.error("Invalid event type {}", dto.type());
                return;
            }

            event.setAmount(dto.amount().doubleValue());
            event.setTimestamp(dto.timestamp());

            repository.save(event);

            log.info(
                    "Consumed and saved event {} for account {}",
                    dto.eventId(),
                    dto.accountId()
            );

        } finally {
            MDC.clear(); // 🚨 MUST — prevents trace leakage
        }
    }
}
