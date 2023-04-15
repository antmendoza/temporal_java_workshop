package io.temporal.step1.moneytransferapp.workflow.activity.moneytransferapp.workflow;

public record TransferRequest(String fromAccountId, String toAccountId, String referenceId, double amount) {
}