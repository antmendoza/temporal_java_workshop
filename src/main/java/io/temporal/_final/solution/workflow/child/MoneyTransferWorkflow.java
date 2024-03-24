package io.temporal._final.solution.workflow.child;

import io.temporal.model.TransferRequest;
import io.temporal.model.TransferResponse;
import io.temporal.model.TransferState;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface has to have at least one method annotated with @WorkflowMethod.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {


    static String createWorkflowId(TransferRequest transferRequest) {
        return "money-transfer-FROM_"+transferRequest.fromAccountId()+"_TO_"+transferRequest.toAccountId();
    }

    // The Workflow method is called by the initiator either via code or CLI.
    @WorkflowMethod
    TransferResponse transfer(TransferRequest transferRequest);

    @SignalMethod
    void approveTransfer(TransferState transferApproved);

    @QueryMethod
    TransferRequest getTransferRequest();
}
