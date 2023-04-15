package io.temporal.step10.moneytransferapp.workflow;

public record TransferRequest(String fromAccountId, String toAccountId, String referenceId, double amount) {
}