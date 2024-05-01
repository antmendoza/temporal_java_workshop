package io.temporal.workshop._1.firstworkflow.initial;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workshop.model.TransferRequest;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {

    
    @WorkflowMethod
    String transfer(TransferRequest transferRequest);
}
