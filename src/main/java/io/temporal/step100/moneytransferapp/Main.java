package io.temporal.step100.moneytransferapp;

import java.util.concurrent.CompletableFuture;

public class Main {

  public static void main(String[] args) {

    // worker
    CompletableFuture.runAsync(
        () -> {
          Worker.main(args);
        });

    // start workflow
    CompletableFuture.runAsync(
        () -> {
          int numRequest = 10;
          ClientStartRequest.startTransfer(numRequest);
        });

    // start workflow
    CompletableFuture.runAsync(
        () -> {
          ListCompletedWorkflows.main(args);
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
