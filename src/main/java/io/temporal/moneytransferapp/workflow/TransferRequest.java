package io.temporal.moneytransferapp.workflow;

public record TransferRequest(String fromAccountId, String toAccountId, String referenceId, double amount) {
}