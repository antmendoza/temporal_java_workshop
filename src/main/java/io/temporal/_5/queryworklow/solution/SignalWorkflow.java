package io.temporal._5.queryworklow.solution;

import io.temporal._5.queryworklow.solution.workflow.MoneyTransferWorkflow;
import io.temporal._5.queryworklow.solution.workflow.TRANSFER_APPROVED;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.Optional;

public class SignalWorkflow {

    public static void main(String[] args) {
        signalWorkflow(TRANSFER_APPROVED.YES);
    }

    public static void signalWorkflow(TRANSFER_APPROVED yes) {
        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        final WorkflowClient client = WorkflowClient.newInstance(service);

        final MoneyTransferWorkflow workflowStub =
                client.newWorkflowStub(MoneyTransferWorkflow.class, Starter.MY_BUSINESS_ID, Optional.empty());
        workflowStub.approveTransfer(yes);

        // newUntypedWorkflowStub
        // TODO
    }
}
