package io.temporal._final.solution.workflow.child;

import io.temporal.model.TransferRequest;
import io.temporal.model.TransferResponse;
import io.temporal.model.TransferStatus;
import io.temporal.workflow.*;

/**
 * Workflow interface has to have at least one method annotated with @WorkflowMethod.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {


    // The Workflow method is called by the initiator either via code or CLI.
    @WorkflowMethod
    TransferResponse transfer(TransferRequest transferRequest);

    //We use signal to introduce the concept. Better approach for this use-case is
    // UpdateWorkflow, which will be introduced latter
    @SignalMethod
    void changeTransferStatus(TransferStatus transferStatus);

    @QueryMethod
    TransferRequest getTransferRequest();
}
