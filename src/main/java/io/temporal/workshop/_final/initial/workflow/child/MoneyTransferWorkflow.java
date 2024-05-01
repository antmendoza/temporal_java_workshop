package io.temporal.workshop._final.initial.workflow.child;

import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferResponse;
import io.temporal.workshop.model.TransferStatus;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {


    
    @WorkflowMethod
    TransferResponse transfer(TransferRequest transferRequest);

    @SignalMethod
    void setTransferStatus(TransferStatus transferStatus);

    @QueryMethod
    TransferRequest getTransferRequest();
}