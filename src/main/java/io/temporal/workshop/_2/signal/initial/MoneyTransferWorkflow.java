package io.temporal.workshop._2.signal.initial;


import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferResponse;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {



    @WorkflowMethod
    TransferResponse transfer(TransferRequest transferRequest);

    
    //@SignalMethod
    //void setTransferStatus(TransferStatus transferStatus);
}
