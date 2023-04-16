package io.temporal.step1.moneytransferapp;

import io.temporal.step1.moneytransferapp.worker.WorkflowWorker;

import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) {
        //start worker
        CompletableFuture.runAsync(() -> {
            WorkflowWorker.main(args);
        });

        //start workflow
        CompletableFuture.runAsync(() -> {
            Starter.main(args);
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
