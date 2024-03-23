package io.temporal._4.signalworkflow.solution.workflow;

import io.temporal.model.TransferRequest;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface has to have at least one method annotated with @WorkflowMethod.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {

    // The Workflow method is called by the initiator either via code or CLI.
    @WorkflowMethod
    void transfer(TransferRequest transferRequest);

    @SignalMethod
    void approveTransfer(TRANSFER_APPROVED transferApproved);
}
