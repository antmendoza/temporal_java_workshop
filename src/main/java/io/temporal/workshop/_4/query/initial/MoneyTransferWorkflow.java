package io.temporal.workshop._4.query.initial;

import io.temporal.workshop.model.TransferRequest;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {


    
    @WorkflowMethod
    void transfer(TransferRequest transferRequest);

    @QueryMethod
    TransferRequest getTransferRequest();
}
