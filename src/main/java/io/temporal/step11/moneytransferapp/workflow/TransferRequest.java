package io.temporal.step11.moneytransferapp.workflow;

public record TransferRequest(String fromAccountId, String toAccountId, String referenceId, double amount) {
}