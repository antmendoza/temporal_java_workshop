package io.temporal.model;

public record TransferRequest(
        String fromAccountId, String toAccountId, double amount) {
}
