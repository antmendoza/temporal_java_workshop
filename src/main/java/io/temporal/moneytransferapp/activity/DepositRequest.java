package io.temporal.moneytransferapp.activity;

public record DepositRequest(String accountId, String referenceId, double amount) {
}