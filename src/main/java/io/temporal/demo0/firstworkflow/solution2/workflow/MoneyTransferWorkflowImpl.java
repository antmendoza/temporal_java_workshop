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

package io.temporal.demo0.firstworkflow.solution2.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.demo0.firstworkflow.solution2.workflow.activity.AccountService;
import io.temporal.model.TransferRequest;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

  private final AccountService accountService =
      Workflow.newActivityStub(
          AccountService.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());

  private final Logger log =
      Workflow.getLogger(
          io.temporal.demo0.firstworkflow.solution2.workflow.MoneyTransferWorkflowImpl.class
              .getSimpleName());

  @Override
  public String transfer(TransferRequest transferRequest) {
    log.info("Init transfer: " + transferRequest);

    accountService.withdraw(
        new io.temporal.service.AccountService.WithdrawRequest(
            transferRequest.fromAccountId(),
            transferRequest.referenceId(),
            transferRequest.amount()));

    // Exception
    accountService.deposit(
        new io.temporal.service.AccountService.DepositRequest(
            transferRequest.toAccountId(),
            transferRequest.referenceId(),
            transferRequest.amount()));

    log.info("End transfer: " + transferRequest);

    return "done";
  }
}
