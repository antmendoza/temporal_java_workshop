package io.temporal._final.httpserver;

import io.temporal.model.AccountSummaryResponse;

public record AccountInfoView(String workflowId, AccountSummaryResponse accountSummary, String status) {
}
