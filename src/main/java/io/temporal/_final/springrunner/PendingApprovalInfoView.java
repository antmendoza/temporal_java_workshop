package io.temporal._final.springrunner;

import io.temporal.model.TransferRequest;

public record PendingApprovalInfoView( String workflowId,  TransferRequest transferRequest) {
}
