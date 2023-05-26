package io.temporal.exercise0.firstworkflow.initial;

import io.temporal.model.TransferRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MoneyTransferWorkflow {

    // The Workflow method is called by the initiator either via code or CLI.
    @WorkflowMethod
    String transfer(TransferRequest transferRequest);
}