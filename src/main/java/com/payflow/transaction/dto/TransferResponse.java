package com.payflow.transaction.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        String reference,
        BigDecimal amount,
        String currency,
        String status
) {
}