package io.temporal.workshop._2.signals.initial;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.workshop.Constants;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferStatus;

import static io.temporal.workshop._2.signals.initial.Starter.MY_BUSINESS_ID;

public class Signal {


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


        // Create the workflow client stub to communicate with the execution
        final MoneyTransferWorkflow workflow =
                client.newWorkflowStub(MoneyTransferWorkflow.class, MY_BUSINESS_ID);


        //workflow.setTransferStatus(TransferStatus.Approved);
        System.exit(0);

    }
}
