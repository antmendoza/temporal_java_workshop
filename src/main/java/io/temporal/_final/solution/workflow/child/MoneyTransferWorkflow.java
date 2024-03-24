package io.temporal._final.solution.workflow.child;

import com.google.protobuf.MessageLite;
import io.temporal.model.TransferRequest;
import io.temporal.model.TransferResponse;
import io.temporal.model.TransferStatus;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface has to have at least one method annotated with @WorkflowMethod.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {


    static String createWorkflowId(TransferRequest transferRequest) {
        return "money-transfer-from["+transferRequest.fromAccountId()+"]-to["+transferRequest.toAccountId()+"]";
    }

    // The Workflow method is called by the initiator either via code or CLI.
    @WorkflowMethod
    TransferResponse transfer(TransferRequest transferRequest);

    @SignalMethod
    void approveTransfer(TransferStatus transferApproved);
}
