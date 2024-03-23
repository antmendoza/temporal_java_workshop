package io.temporal.model;

public record Transfer(String requestId, String targetCustomer, double amount) {
}
