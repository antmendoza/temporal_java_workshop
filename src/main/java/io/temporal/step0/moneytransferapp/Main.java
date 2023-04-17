package io.temporal.step0.moneytransferapp;

import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) {

        //start workflow
        CompletableFuture.runAsync(() -> {
            ClientStartRequest.main(args);
        });


        //start worker
        CompletableFuture.runAsync(() -> {
            Worker.main(args);
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
