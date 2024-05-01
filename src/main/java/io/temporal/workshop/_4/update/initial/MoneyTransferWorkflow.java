package io.temporal.workshop._4.update.initial;


import io.temporal.workflow.UpdateMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferResponse;
import io.temporal.workshop.model.TransferStatus;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {



    @WorkflowMethod
    TransferResponse transfer(TransferRequest transferRequest);


   // @UpdateMethod
   // String setTransferStatus(TransferStatus transferStatus);

}
