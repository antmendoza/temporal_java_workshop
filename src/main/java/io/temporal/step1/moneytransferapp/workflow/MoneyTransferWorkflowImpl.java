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

package io.temporal.step1.moneytransferapp.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.model.TransferRequest;
import io.temporal.services.DepositRequest;
import io.temporal.services.WithdrawRequest;
import io.temporal.step1.moneytransferapp.workflow.activity.AccountService;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;


public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    public static final String TASK_QUEUE = "MoneyTransfer";
    final AccountService accountService = Workflow.newActivityStub(AccountService.class, ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(3))
            .setRetryOptions(RetryOptions.newBuilder().build())
            .build());
    private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());

    @Override
    public void transfer(TransferRequest transferRequest) {
        log.info("Init transfer: " + transferRequest);

        double amount = transferRequest.amount();
        accountService.withdraw(new WithdrawRequest(transferRequest.fromAccountId(), transferRequest.referenceId(), amount));
        if(amount > 20){
            //This does not make any sense, the purpose is to demostrate how, in presence of failure,
            // Temporal recovers the execution state and continue from the point the execution stopped
            String a = null;
            String b = a.toString();
        }
        accountService.deposit(new DepositRequest(transferRequest.toAccountId(), transferRequest.referenceId(), amount));

        log.info("End transfer: " + transferRequest);

    }


    public static void main(String[] args) {

        double commision = 5.3 / 0; //Introducing an error at runtime to demostrate


    }
}
