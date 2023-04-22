package io.temporal.step10.moneytransferapp;

import java.util.concurrent.CompletableFuture;

public class Main {

  public static void main(String[] args) {
    // start worker
    CompletableFuture.runAsync(
        () -> {
          WorkerProcess.main(args);
        });

    // start workflow
    CompletableFuture.runAsync(
        () -> {
          StartRequest.main(args);
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
