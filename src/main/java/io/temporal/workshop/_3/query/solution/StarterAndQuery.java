package io.temporal.workshop._3.query.solution;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.workshop.Constants;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferResponse;
import io.temporal.workshop.model.TransferStatus;

public class StarterAndQuery {

    static final String MY_BUSINESS_ID = StarterAndQuery.class.getPackageName() + ":money-transfer";

    public static void main(String[] args) throws InterruptedException {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(Constants.targetGRPC)
                .build());

        final WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions
                .newBuilder()
                .setNamespace(Constants.namespace)
                .build());

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
                new TransferRequest("fromAccount", "toAccount", 200);


        // Sync, blocking invocation
        // workflow.transfer(transferRequest);
        // Async invocation, won't block
        WorkflowClient.start(workflow::transfer, transferRequest);


        // Query
        TransferStatus transferStatusAfterStart = getTransferStatus(client).getStatus();
        System.out.println("TransferStatusAfterStart: " + transferStatusAfterStart);
        final int sleepMs = 6000;
        System.out.println("Waiting " + sleepMs /1000+ " seconds");
        Thread.sleep(sleepMs);
        TransferStatus transferStatusAfter6Seconds = getTransferStatus(client).getStatus();
        System.out.println("TransferStatusAfter6Seconds: " + transferStatusAfter6Seconds);


        //Result
        TransferResponse result = client.newUntypedWorkflowStub(MY_BUSINESS_ID).getResult(TransferResponse.class);
        System.out.println("Result " + result);

        System.exit(0);
    }

    private static MoneyTransferWorkflow getTransferStatus(final WorkflowClient client) {
        return client.newWorkflowStub(MoneyTransferWorkflow.class, MY_BUSINESS_ID);
    }
}
