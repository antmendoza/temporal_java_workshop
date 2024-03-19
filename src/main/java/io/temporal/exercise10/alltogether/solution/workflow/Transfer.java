package io.temporal.exercise10.alltogether.solution.workflow;

public record Transfer(String requestId, String targetCustomer, double amount) {
}
