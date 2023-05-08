/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.temporal.service;

public class BankingClient {

  public void withdraw(WithdrawRequest withdrawRequest) {
    System.out.println("Withdraw init: " + withdrawRequest);
    randomSleep();
    System.out.println("Withdraw end: " + withdrawRequest);
  }

  public void deposit(DepositRequest depositRequest) {
    System.out.println("Deposit init: " + depositRequest);
    randomSleep();
    System.out.println("Deposit end: " + depositRequest);
  }

  private static void randomSleep() {
    try {
      long millis = (long) (Math.random() * 1000);
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
