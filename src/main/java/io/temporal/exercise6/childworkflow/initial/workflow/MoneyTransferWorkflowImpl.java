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

package io.temporal.exercise6.childworkflow.initial.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.model.TransferRequests;
import io.temporal.service.AccountService;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

  private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());
  private final AccountService accountService =
      Workflow.newActivityStub(
          AccountService.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());

  @Override
  public void transfer(TransferRequests transferRequests) {
    log.info("Init transfer size: " + transferRequests.transferRequests().size());

    final List<Promise<Void>> promises = new ArrayList<>();

    transferRequests
        .transferRequests()
        .forEach(
            transferRequest -> {
              log.info("Init transfer: " + transferRequest);

              accountService.withdraw(
                  new WithdrawRequest(
                      transferRequest.fromAccountId(),
                      transferRequest.referenceId(),
                      transferRequest.amount()));
              accountService.deposit(
                  new DepositRequest(
                      transferRequest.toAccountId(),
                      transferRequest.referenceId(),
                      transferRequest.amount()));

              log.info("End transfer: " + transferRequest);
            });

    log.info("End transfer size: " + transferRequests.transferRequests().size());
  }
}
