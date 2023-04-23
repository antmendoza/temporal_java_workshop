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

package io.temporal.demo1.activityretry.workflow.activity;

import io.temporal.activity.Activity;
import io.temporal.services.BankingClient;
import io.temporal.services.DepositRequest;
import io.temporal.services.WithdrawRequest;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class AccountServiceImpl implements AccountService {

  private final Logger log = Workflow.getLogger(AccountServiceImpl.class.getSimpleName());

  private final BankingClient bankingClient;

  public AccountServiceImpl(BankingClient bankingClient) {
    this.bankingClient = bankingClient;
  }

  @Override
  public void withdraw(WithdrawRequest withdrawRequest) {
    log.info("Init withdraw : " + withdrawRequest);
    this.bankingClient.withdraw(withdrawRequest);
    log.info("End withdraw : " + withdrawRequest);
  }

  @Override
  public void deposit(DepositRequest depositRequest) {
    log.info("Init deposit : " + depositRequest);

    simulateServiceIsDownAndSuccessAfterNumIteractions(4);

    this.bankingClient.deposit(depositRequest);
    log.info("End deposit : " + depositRequest);
  }

  private void simulateServiceIsDownAndSuccessAfterNumIteractions(int numIterationsBeforeSuccess) {

    int attend = Activity.getExecutionContext().getInfo().getAttempt();
    if (attend <= numIterationsBeforeSuccess) {
      String message = "Error: Can not reach service...attend " + attend;
      throw new RuntimeException(message);
      // throw ApplicationFailure.newFailure(message, "ServiceUnreachable");
      // throw ApplicationFailure.newNonRetryableFailure(message,"ServiceUnreachable" );
    }
  }
}
