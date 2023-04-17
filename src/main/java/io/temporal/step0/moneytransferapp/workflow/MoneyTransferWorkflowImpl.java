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

package io.temporal.step0.moneytransferapp.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.model.TransferRequest;
import io.temporal.services.DepositRequest;
import io.temporal.services.WithdrawRequest;
import io.temporal.step0.moneytransferapp.workflow.activity.AccountService;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;


public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());

    public static final String TASK_QUEUE = "MoneyTransfer";

    final AccountService accountService = Workflow.newActivityStub(AccountService.class, ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(3))
            .setRetryOptions(RetryOptions.newBuilder().build())
            .build());



    @Override
    public void transfer(TransferRequest transferRequest) {
        log.info("init transfer: "+ transferRequest);

        accountService.withdraw(new WithdrawRequest(transferRequest.fromAccountId(), transferRequest.referenceId(), transferRequest.amount()));
        accountService.deposit(new DepositRequest(transferRequest.toAccountId(), transferRequest.referenceId(), transferRequest.amount()));

        log.info("end transfer: "+ transferRequest);

    }
}