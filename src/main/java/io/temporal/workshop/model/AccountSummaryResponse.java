package io.temporal.workshop.model;

import java.util.List;

public record AccountSummaryResponse(Account account, List<Operation> operations) {
}
