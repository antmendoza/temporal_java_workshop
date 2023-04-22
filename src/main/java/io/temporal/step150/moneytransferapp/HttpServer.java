package io.temporal.step150.moneytransferapp;

import io.temporal.step150.moneytransferapp.httpserver.CryptCodec;
import io.temporal.step150.moneytransferapp.httpserver.RDEHttpServer;
import java.io.IOException;
import java.util.Collections;

public class HttpServer {

  public static void main(String[] args) {
    try {
      System.out.println("Starting http server... ");
      new RDEHttpServer(Collections.singletonList(new CryptCodec()), 8888).start();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
