package io.temporal.step3.moneytransferapp;

import io.temporal.step3.moneytransferapp.worker.WorkflowWorker;
import io.temporal.step3.moneytransferapp.workflow.TRANSFER_APPROVED;

import java.util.concurrent.CompletableFuture;

public class Main {


    public static void main(String[] args) {


        //worker
        CompletableFuture.runAsync(() -> {WorkflowWorker.main(args);});

        //start workflow
        CompletableFuture.runAsync(() -> {Starter.main(args);});
        waitMillis(1000);

        //query workflow
        CompletableFuture.runAsync(() -> {QueryWorkflow.main(args);});
        waitMillis(5000);

        //signal workflow
        CompletableFuture.runAsync(() -> {SignalWorkflow.signalWorkflow(TRANSFER_APPROVED.YES);});
        waitMillis(2000);

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
