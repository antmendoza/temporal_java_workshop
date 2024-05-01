package io.temporal.workshop._1.firstworkflow.solution;

import io.temporal.workshop.model.TransferRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {

    
    @WorkflowMethod
    String transfer(TransferRequest transferRequest);
}
