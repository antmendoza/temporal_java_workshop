package io.temporal.workshop._3.query.initial;


import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferResponse;
import io.temporal.workshop.model.TransferStatus;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {


    @WorkflowMethod
    TransferResponse transfer(TransferRequest transferRequest);

    
    //@QueryMethod
    //TransferStatus getStatus();
}
