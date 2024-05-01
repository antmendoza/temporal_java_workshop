package io.temporal.workshop._final.springrunner;

import io.temporal.workshop.model.TransferRequest;

public record PendingRequestInfoView(String workflowId, TransferRequest transferRequest) {
}
