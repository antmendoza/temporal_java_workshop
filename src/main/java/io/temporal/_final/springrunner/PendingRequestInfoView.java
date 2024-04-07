package io.temporal._final.springrunner;

import io.temporal.model.TransferRequest;

public record PendingRequestInfoView(String workflowId, TransferRequest transferRequest) {
}
