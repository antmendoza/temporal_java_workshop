package io.temporal.moneytransferapp.activity;

public record WithdrawRequest(String accountId, String referenceId, double amount) {
}