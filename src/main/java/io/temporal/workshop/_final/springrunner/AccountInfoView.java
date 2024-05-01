package io.temporal.workshop._final.springrunner;

import io.temporal.workshop.model.AccountSummaryResponse;

public record AccountInfoView(String workflowId, AccountSummaryResponse accountSummary, String status) {
}
