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

package io.temporal.exercise1.activityretry.workflow;

import io.temporal.activity.Activity;
import io.temporal.service.AccountService;
import io.temporal.service.BankingClient;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class AccountServiceImplRetry implements AccountService {

  private final Logger log = Workflow.getLogger(AccountServiceImplRetry.class.getSimpleName());

  private final BankingClient bankingClient;

  public AccountServiceImplRetry(BankingClient bankingClient) {
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

    simulateServiceIsDownAndSuccessAfterNumAttempts(4);

    this.bankingClient.deposit(depositRequest);
    log.info("End deposit : " + depositRequest);
  }

  private void simulateServiceIsDownAndSuccessAfterNumAttempts(int numAttemptsBeforeSuccess) {

    int attend = Activity.getExecutionContext().getInfo().getAttempt();
    log.info("Attend number : " + attend);

    if (attend <= numAttemptsBeforeSuccess) {
      String message = "Error: Can not reach service...Number of attend: " + attend;
      throw new RuntimeException(message);
      // throw ApplicationFailure.newFailure(message, "ServiceUnreachable");
      // throw ApplicationFailure.newNonRetryableFailure(message,"ServiceUnreachable" );
    }
  }
}
