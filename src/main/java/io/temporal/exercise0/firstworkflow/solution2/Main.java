package io.temporal.exercise0.firstworkflow.solution2;

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
