package io.temporal._5.queryworklow.initial;

import io.temporal._5.queryworklow.initial.workflow.TRANSFER_APPROVED;

import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) {

        // worker
        CompletableFuture.runAsync(
                () -> {
                    WorkerProcess.main(args);
                });

        // start workflow
        CompletableFuture.runAsync(
                () -> {
                    Starter.main(args);
                });
        waitMillis(2000);

        // query workflow
        CompletableFuture.runAsync(
                () -> {
                    // query workflow
                });
        waitMillis(5000);

        // signal workflow
        CompletableFuture.runAsync(
                () -> {
                    SignalWorkflow.signalWorkflow(TRANSFER_APPROVED.YES);
                });
        waitMillis(4000);

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
