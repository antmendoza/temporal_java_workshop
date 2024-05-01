package io.temporal.workshop._0.demo.activityretry;

import io.temporal.workshop.model.TransferRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {

    
    @WorkflowMethod
    void transfer(TransferRequest transferRequest);
}
