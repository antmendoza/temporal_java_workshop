package io.temporal.step11.moneytransferapp;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflowservice.v1.GetWorkflowExecutionHistoryRequest;
import io.temporal.api.workflowservice.v1.GetWorkflowExecutionHistoryResponse;
import io.temporal.model.TransferRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class DecodePayload {

    private static final String MY_BUSINESS_ID = "money-transfer";

    public static void main(String[] args) {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();


        final GetWorkflowExecutionHistoryRequest req = GetWorkflowExecutionHistoryRequest.newBuilder()
                .setExecution(WorkflowExecution.newBuilder().setWorkflowId(MY_BUSINESS_ID).build())
                .setNamespace("default").build();


        final GetWorkflowExecutionHistoryResponse res =
                service.blockingStub().getWorkflowExecutionHistory(req);


        res.getHistory().getEvents(0).getWorkflowExecutionStartedEventAttributes().getInput().getPayloadsList()
                .forEach(payload -> {
                    System.out.println("Payload: " + new MyCustomDataConverter().fromPayload(payload,
                            TransferRequest.class, TransferRequest.class));

                });


    }
}
