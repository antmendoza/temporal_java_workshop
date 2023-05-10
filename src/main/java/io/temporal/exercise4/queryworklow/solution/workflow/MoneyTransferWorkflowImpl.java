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

package io.temporal.exercise4.queryworklow.solution.workflow;

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
  private TRANSFER_APPROVED transferApproved;
  private TRANSFER_STATUS transferStatus = null;

  @Override
  public void transfer(TransferRequest transferRequest) {

    transferStatus = TRANSFER_STATUS.INITIATED;

    log.info("Init transfer: " + transferRequest);

    if (transferRequest.amount() > 1000) {
      transferStatus = TRANSFER_STATUS.WAITING_APPROVAL;

      log.info("request need approval: " + transferRequest);

      Workflow.await(() -> transferApproved != null);

      log.info("transferApproved: " + transferApproved);

      if (TRANSFER_APPROVED.NO.equals(transferApproved)) {
        // notify customer...
        log.info("notify customer, transferApproved: " + transferRequest);
        transferStatus = TRANSFER_STATUS.DENIED;
        return;
      }

      transferStatus = TRANSFER_STATUS.APPROVED;
    }

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

    transferStatus = TRANSFER_STATUS.COMPLETED;

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
