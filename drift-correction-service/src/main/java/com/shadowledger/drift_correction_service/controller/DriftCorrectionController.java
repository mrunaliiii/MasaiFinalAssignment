package com.shadowledger.drift_correction_service.controller;



import com.shadowledger.drift_correction_service.dto.CbsBalanceDto;
import com.shadowledger.drift_correction_service.dto.CorrectionEventDto;
import com.shadowledger.drift_correction_service.service.DriftCorrectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Slf4j
@RestController
public class DriftCorrectionController {

    private final DriftCorrectionService service;

    public DriftCorrectionController(DriftCorrectionService service) {
        this.service = service;
    }

    @PostMapping("/drift-check")
    public ResponseEntity<List<CorrectionEventDto>> checkDrift(
            @RequestBody List<CbsBalanceDto> balances) {

        log.info("Drift check requested for {} accounts", balances.size());

        List<CorrectionEventDto> corrections = balances.stream()
                .map(b -> service.generateCorrectionEvent(
                        b, service.getShadowBalance(b.getAccountId())))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Generated {} correction events", corrections.size());
        return ResponseEntity.ok(corrections);
    }

    @PostMapping("/correct/{accountId}")
    public ResponseEntity<CorrectionEventDto> manualCorrection(
            @PathVariable String accountId,
            @RequestParam String type,
            @RequestParam Double amount) {

        log.info("Manual correction requested for account {}", accountId);

        CorrectionEventDto correction =
                service.createManualCorrection(accountId, type, amount);

        return ResponseEntity.ok(correction);
    }
}
