package io.temporal.step20.moneytransferapp;

import io.temporal.step20.moneytransferapp.workflow.TRANSFER_APPROVED;

import java.util.concurrent.CompletableFuture;

public class Main {


    public static void main(String[] args) {
        //start worker
        CompletableFuture.runAsync(() -> {
            Worker.main(args);
        });

        //start workflow
        CompletableFuture.runAsync(() -> {
            ClientStartRequest.main(args);
        });
        waitMillis(2000);

        //signal workflow
        CompletableFuture.runAsync(() -> {
            SignalWorkflow.signalWorkflow(TRANSFER_APPROVED.YES);
        });
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