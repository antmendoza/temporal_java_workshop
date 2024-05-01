package io.temporal.workshop._4.update.initial;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.workshop.Constants;
import io.temporal.workshop.model.TransferStatus;

import java.util.Date;


public class Update {


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
                client.newWorkflowStub(MoneyTransferWorkflow.class, Starter.MY_BUSINESS_ID);

        Date beforeUpdate = new Date();

        //String updateResult = workflow.setTransferStatus(TransferStatus.Approved);
        //System.out.println("Update result: "+ updateResult);

        int secondsBetween = (int) (new Date().getTime() - (beforeUpdate.getTime() ) / 1000);

        System.out.println("After "+secondsBetween+" seconds");

        System.exit(0);

    }
}
