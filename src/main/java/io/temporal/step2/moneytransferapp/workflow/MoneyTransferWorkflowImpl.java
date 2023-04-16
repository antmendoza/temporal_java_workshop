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

package io.temporal.step2.moneytransferapp.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.model.TransferRequest;
import io.temporal.step2.moneytransferapp.workflow.activity.AccountService;
import io.temporal.step2.moneytransferapp.workflow.activity.DepositRequest;
import io.temporal.step2.moneytransferapp.workflow.activity.WithdrawRequest;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

/**
 * GreetingWorkflow implementation that calls GreetingsActivities#composeGreeting.
 */
public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    public static final String TASK_QUEUE = "MoneyTransfer";
    final AccountService accountService = Workflow.newActivityStub(AccountService.class, ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(1))
            .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
            .build());
    private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());
    private TRANSFER_APPROVED transferApproved;

    @Override
    public void transfer(TransferRequest transferRequest) {

        log.info("init transfer: " + transferRequest);

        boolean needApproval = false;

        if (transferRequest.amount() > 1000) {

            needApproval = true;
            log.info("request need approval: " + transferRequest);

            Workflow.await(() -> transferApproved != null);
            log.info("transferApproved: " + transferApproved);

        }


        if (!needApproval || transferApproved.equals(TRANSFER_APPROVED.YES)) {
            accountService.withdraw(new WithdrawRequest(transferRequest.fromAccountId(), transferRequest.referenceId(), transferRequest.amount()));
            accountService.deposit(new DepositRequest(transferRequest.toAccountId(), transferRequest.referenceId(), transferRequest.amount()));
        }


        if (transferApproved.equals(TRANSFER_APPROVED.NO)) {
            //notify customer...
            log.info("notify customer, transferApproved: " + transferRequest);
        }


        log.info("end transfer: " + transferRequest);

    }

    @Override
    public void approveTransfer(TRANSFER_APPROVED transferApproved) {
        this.transferApproved = transferApproved;
    }
}
