package io.temporal.demo10.childworkflow.solution.parallel;

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
          StartRequest.startTransfer(numRequest);
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
