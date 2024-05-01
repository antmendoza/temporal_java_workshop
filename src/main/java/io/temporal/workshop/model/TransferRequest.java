package io.temporal.workshop.model;

public record TransferRequest(
        String fromAccountId, String toAccountId, double amount) {
}
