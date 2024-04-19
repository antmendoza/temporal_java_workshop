package io.temporal._4.query.initial;

import io.temporal.model.TransferRequest;
import io.temporal.model.TransferStatus;
import io.temporal.workflow.QueryMethod;
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

    @QueryMethod
    TransferRequest getTransferRequest();
}
