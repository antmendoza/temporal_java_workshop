package io.temporal._1.firstworkflow.solution1;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal._1.firstworkflow.solution1.workflow.MoneyTransferWorkflow;
import io.temporal.model.TransferRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {

    static final String MY_BUSINESS_ID = Starter.class.getPackageName() + ":money-transfer";

    public static void main(String[] args) {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        final WorkflowClient client = WorkflowClient.newInstance(service);

        final WorkflowOptions options =
                WorkflowOptions.newBuilder()
                        .setWorkflowId(MY_BUSINESS_ID)
                        .setTaskQueue(WorkerProcess.TASK_QUEUE)
                        .build();

        // Create the workflow client stub.
        // It is used to start our workflow execution.
        final MoneyTransferWorkflow workflow =
                client.newWorkflowStub(MoneyTransferWorkflow.class, options);

        TransferRequest transferRequest =
                new TransferRequest("fromAccount", "toAccount", "referenceId", 200);
        // Sync, blocking invocation
        // workflow.transfer(transferRequest);

        // Async
        WorkflowClient.start(workflow::transfer, transferRequest);
        // block and wait execution to finish
        String result = client.newUntypedWorkflowStub(MY_BUSINESS_ID).getResult(String.class);
        System.out.println("Result " + result);
    }
}
