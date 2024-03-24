package io.temporal._3.workflowtaskfretry;

import io.temporal._3.workflowtaskfretry.workflow.MoneyTransferWorkflow;
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

        // Create the workflow client stub. It is used to start our workflow execution.
        final WorkflowOptions build =
                WorkflowOptions.newBuilder()
                        .setWorkflowId(MY_BUSINESS_ID)
                        .setTaskQueue(WorkerProcess.TASK_QUEUE)
                        .build();

        final MoneyTransferWorkflow workflow =
                client.newWorkflowStub(MoneyTransferWorkflow.class, build);

        TransferRequest transferRequest =
                new TransferRequest("fromAccount", "toAccount", 200);
        workflow.transfer(transferRequest);
    }
}
