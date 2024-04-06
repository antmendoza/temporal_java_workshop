package io.temporal._final.solution.workflow.child;

import io.temporal.model.TransferRequest;
import io.temporal.model.TransferResponse;
import io.temporal.model.TransferState;
import io.temporal.workflow.*;

/**
 * Workflow interface has to have at least one method annotated with @WorkflowMethod.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {


    // The Workflow method is called by the initiator either via code or CLI.
    @WorkflowMethod
    TransferResponse transfer(TransferRequest transferRequest);

    //We use signal to introduce the concept, a better approach is to use
    // UpdateWorkflow. We will introduce UpdateWorkflow latter
    @SignalMethod
    void approveTransfer(TransferState transferApproved);

    @QueryMethod
    TransferRequest getTransferRequest();
}
