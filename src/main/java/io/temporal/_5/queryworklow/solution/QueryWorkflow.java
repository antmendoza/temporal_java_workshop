package io.temporal._5.queryworklow.solution;

import io.temporal._5.queryworklow.solution.workflow.MoneyTransferWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.Optional;

public class QueryWorkflow {

    public static void main(String[] args) {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        final WorkflowClient client = WorkflowClient.newInstance(service);

        final MoneyTransferWorkflow workflowStub =
                client.newWorkflowStub(
                        MoneyTransferWorkflow.class, Starter.MY_BUSINESS_ID, Optional.empty());

        while (true) {
            System.out.println("queryStatus result: " + workflowStub.queryStatus());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // newUntypedWorkflowStub
        // TODO

    }
}
