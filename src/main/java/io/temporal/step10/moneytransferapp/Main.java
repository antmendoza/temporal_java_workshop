package io.temporal.step10.moneytransferapp;

import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.step10.moneytransferapp.worker.WorkflowWorker;

import java.util.concurrent.CompletableFuture;

public class Main {


    public static void main(String[] args) {


        //worker
        CompletableFuture.runAsync(() -> {WorkflowWorker.main(args);});

        //start workflow
        CompletableFuture.runAsync(() -> {
            int numRequest = 10;
            Starter.startTransfer(numRequest);});



        //start workflow
        CompletableFuture.runAsync(() -> {

            final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
            while (true){

                int numClosedExecution = service.blockingStub().listWorkflowExecutions(
                        // https://docs.temporal.io/visibility#search-attribute
                        ListWorkflowExecutionsRequest.newBuilder()
                                .setQuery("ExecutionStatus=\"Completed\" and WorkflowType=\"MoneyTransferChildWorkflow\"")
                                .setNamespace("default").build()
                ).getExecutionsCount();

                System.out.println(">>>>> numClosedExecution " + numClosedExecution);

                waitMillis(100);
            }
        });

        waitMillis(5000);
        System.exit(0);


    }

    private static void waitMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
