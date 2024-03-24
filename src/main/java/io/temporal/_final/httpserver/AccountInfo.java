package io.temporal._final.httpserver;

public record AccountInfo(String workflowId, io.temporal.model.AccountSummaryResponse accountSummary) {
}
