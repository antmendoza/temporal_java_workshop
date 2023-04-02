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

package io.temporal.moneytransferapp.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.moneytransferapp.activity.AccountService;
import io.temporal.moneytransferapp.activity.DepositRequest;
import io.temporal.moneytransferapp.activity.WithdrawRequest;
import io.temporal.workflow.Workflow;

import java.time.Duration;

/**
 * GreetingWorkflow implementation that calls GreetingsActivities#composeGreeting.
 */
public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    //private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());

    public static final String TASK_QUEUE = "MoneyTransfer";


    @Override
    public String transfer(String fromAccountId, String toAccountId, String referenceId, double amount) {
        System.out.println("Hello from JOTB23");


        final AccountService accountService = Workflow.newActivityStub(AccountService.class, ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(1))
                .build());

        accountService.withdraw(new WithdrawRequest(fromAccountId, referenceId, amount));
        accountService.deposit(new DepositRequest(toAccountId, referenceId, amount));

        return "some-transference-id";
    }
}
