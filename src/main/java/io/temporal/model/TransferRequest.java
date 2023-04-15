package io.temporal.model;

public record TransferRequest(String fromAccountId, String toAccountId, String referenceId, double amount) {
}