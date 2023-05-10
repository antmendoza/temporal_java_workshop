package io.temporal.exercise6.childworkflow.initial;

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
          int numRequest = 10;
          Starter.startTransfer(numRequest);
        });

    // start workflow
    CompletableFuture.runAsync(
        () -> {
          //  ListCompletedWorkflows.main(args);
        });

    waitMillis(20000);
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
