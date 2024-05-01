package io.temporal.workshop._0.demo.activityretry;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.workshop.Constants;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

public class Starter {

    static final String MY_BUSINESS_ID = Starter.class.getPackageName() + ":money-transfer";

    public static void main(String[] args) {

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

        workflow.transfer(new TransferRequest("fromAccount", "toAccount", 200));
    }
}
