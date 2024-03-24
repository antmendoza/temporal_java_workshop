package io.temporal._2.activityretry;

import io.temporal._2.activityretry.workflow.MoneyTransferWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
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
                        .setTaskQueue(WorkerProcess.TASK_QUEUE) // mandatory
                        // .setWorkflowRunTimeout(Duration.ofDays(2))
                        .build();

        // Create the workflow client stub.
        // It is used to start our workflow execution.
        final MoneyTransferWorkflow workflow =
                client.newWorkflowStub(MoneyTransferWorkflow.class, options);

        workflow.transfer(new TransferRequest("fromAccount", "toAccount", 200));
    }
}
