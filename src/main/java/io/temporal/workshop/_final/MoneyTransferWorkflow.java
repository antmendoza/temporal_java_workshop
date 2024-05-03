package io.temporal.workshop._final;

import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferResponse;
import io.temporal.workshop.model.TransferStatus;
import io.temporal.workflow.*;

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
