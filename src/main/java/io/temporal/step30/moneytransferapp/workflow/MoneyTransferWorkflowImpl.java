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

package io.temporal.step30.moneytransferapp.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.model.TransferRequest;
import io.temporal.services.DepositRequest;
import io.temporal.services.WithdrawRequest;
import io.temporal.step30.moneytransferapp.workflow.activity.AccountService;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

  public static final String TASK_QUEUE = "MoneyTransfer";
  final AccountService accountService =
      Workflow.newActivityStub(
          AccountService.class,
          ActivityOptions.newBuilder()
              .setStartToCloseTimeout(Duration.ofSeconds(3))
              .setRetryOptions(RetryOptions.newBuilder().build())
              .build());
  private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());
  private TRANSFER_APPROVED transferApproved;
  private TRANSFER_STATUS transferStatus = null;

  @Override
  public void transfer(TransferRequest transferRequest) {

    transferStatus = TRANSFER_STATUS.INITIATED;

    log.info("Init transfer: " + transferRequest);

    boolean needApproval = false;

    if (transferRequest.amount() > 1000) {
      transferStatus = TRANSFER_STATUS.WAITING_APPROVAL;

      needApproval = true;
      log.info("request need approval: " + transferRequest);

      // comment and uncomment the next block. Stop worker, start workflow, start worker and wait 10
      // seconds
      Workflow.await(() -> transferApproved != null);
      /*
                  boolean authorizationReceived = Workflow.await(Duration.ofSeconds(10),  () -> transferApproved != null);
                  if(!authorizationReceived){
                     log.info("authorization not received: ");
                      return;
                  }
      */

      log.info("transferApproved: " + transferApproved);
    }

    if (!needApproval || transferApproved.equals(TRANSFER_APPROVED.YES)) {
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

      transferStatus = TRANSFER_STATUS.APPROVED;
    }

    if (TRANSFER_APPROVED.NO.equals(transferApproved)) {
      // notify customer...
      log.info("notify customer, transferApproved: " + transferRequest);
      transferStatus = TRANSFER_STATUS.DENIED;
    }

    log.info("End transfer: " + transferRequest);
  }

  @Override
  public void approveTransfer(TRANSFER_APPROVED transferApproved) {
    this.transferApproved = transferApproved;
  }

  @Override
  public TRANSFER_STATUS queryStatus() {
    return transferStatus;
  }
}
