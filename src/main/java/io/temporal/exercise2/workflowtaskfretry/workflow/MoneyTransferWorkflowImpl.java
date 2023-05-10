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

package io.temporal.exercise2.workflowtaskfretry.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.model.TransferRequest;
import io.temporal.service.AccountService;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

  private final AccountService accountService =
      Workflow.newActivityStub(
          AccountService.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());
  private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());

  @Override
  public void transfer(TransferRequest transferRequest) {
    log.info("Init transfer: " + transferRequest);

    double amount = transferRequest.amount();
    accountService.withdraw(
        new WithdrawRequest(
            transferRequest.fromAccountId(), transferRequest.referenceId(), amount));

    double depositAmount = amount;
    if (amount > 20) {
      // The purpose is to demostrate how, in presence of a runtime error,
      // after fixing the code the workflow execution will continue from where the execution was
      // stopped

      // calculate fee
      // TODO fix the code and restart WorkerProcess
      // depositAmount = amount - amount * 0.1;
      depositAmount = amount - (int) amount / 0;
    }
    accountService.deposit(
        new DepositRequest(
            transferRequest.toAccountId(), transferRequest.referenceId(), depositAmount));

    log.info("End transfer: " + transferRequest);
  }
}
