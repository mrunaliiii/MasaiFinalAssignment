package com.shadowledger.drift_correction_service.service;


import com.shadowledger.drift_correction_service.dto.CbsBalanceDto;
import com.shadowledger.drift_correction_service.dto.CorrectionEventDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class DriftCorrectionService {

    private final KafkaTemplate<String, CorrectionEventDto> kafkaTemplate;
    private final RestTemplate restTemplate;

    @Value("${shadow-ledger.service.url:http://localhost:8082}")
    private String shadowLedgerServiceUrl;

    // ✅ Inject RestTemplate (DO NOT create manually)
    public DriftCorrectionService(
            KafkaTemplate<String, CorrectionEventDto> kafkaTemplate,
            RestTemplate restTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    public Double getShadowBalance(String accountId) {
        try {
            String url = shadowLedgerServiceUrl + "/accounts/" + accountId + "/shadow-balance";

            log.info("Fetching shadow balance for account {}", accountId);

            Map<String, Object> response =
                    restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("balance")) {
                return ((Number) response.get("balance")).doubleValue();
            }
        } catch (Exception e) {
            log.error("Failed to fetch shadow balance for account {}", accountId, e);
        }
        return 0.0;
    }

    public CorrectionEventDto generateCorrectionEvent(
            CbsBalanceDto cbsBalance,
            Double shadowBalance
    ) {
        double diff = cbsBalance.getReportedBalance() - shadowBalance;
        if (diff == 0) return null;

        String type = diff > 0 ? "credit" : "debit";

        CorrectionEventDto correction = new CorrectionEventDto(
                "CORR-" + cbsBalance.getAccountId() + "-" +
                        UUID.randomUUID().toString().substring(0, 8),
                cbsBalance.getAccountId(),
                type,
                Math.abs(diff)
        );

        publishToKafka(correction);
        return correction;
    }

    public CorrectionEventDto createManualCorrection(
            String accountId,
            String type,
            Double amount
    ) {
        CorrectionEventDto correction = new CorrectionEventDto(
                "MANUAL-" + accountId + "-" +
                        UUID.randomUUID().toString().substring(0, 8),
                accountId,
                type,
                amount
        );

        publishToKafka(correction);
        return correction;
    }

    /// 🔑 Centralized Kafka send with trace propagation
    private void publishToKafka(CorrectionEventDto correction) {

        ProducerRecord<String, CorrectionEventDto> record =
                new ProducerRecord<>(
                        "transactions.corrections",
                        correction.getAccountId(),
                        correction
                );

        // 🔥 Ensure traceId ALWAYS exists
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        // 🔥 Add traceId to Kafka headers
        record.headers().add(
                "X-Trace-Id",
                traceId.getBytes(StandardCharsets.UTF_8)
        );

        kafkaTemplate.send(record);

        log.info(
                "Published correction event {} for account {}",
                correction.getEventId(),
                correction.getAccountId()
        );
    }

}
