package io.temporal._final.httpserver;

import io.temporal.model.TransferRequest;

public record PendingApprovalInfoView( String workflowId,  TransferRequest transferRequest) {
}
