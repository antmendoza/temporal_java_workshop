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

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.moneytransferapp.activity.AccountService;
import io.temporal.moneytransferapp.activity.AccountServiceImpl;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MoneyTransferWorkflowImplTest {


    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setDoNotStart(true)
                    .build();


    @Test
    public void testTransfer() {


        AccountService accountService = Mockito.mock(AccountServiceImpl.class);


        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(accountService);


        // Start server
        testWorkflowRule.getTestEnvironment().start();


        final WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(testWorkflowRule.getTaskQueue())
                .build();

        final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

        ///////////


        final MoneyTransferWorkflow workflow = workflowClient
                .newWorkflowStub(MoneyTransferWorkflow.class, options);


        //Start workflow
        TransferRequest transferRequest = new TransferRequest("account1",
                "account2",
                "reference1",
                1.23);
        final String result = workflow.transfer(transferRequest);

        Assert.assertEquals(result, "some-transference-id");

        /////////////

    }





    @Test
    public void testRetry() {


        AccountService accountService = Mockito.mock(AccountServiceImpl.class);
        when(accountService.deposit(any())).thenThrow(RuntimeException.class);

        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(accountService);


        // Start server
        testWorkflowRule.getTestEnvironment().start();


        final WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(testWorkflowRule.getTaskQueue())
                .build();

        final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

        ///////////


        final MoneyTransferWorkflow workflow = workflowClient
                .newWorkflowStub(MoneyTransferWorkflow.class, options);


        //Start workflow
        TransferRequest transferRequest = new TransferRequest("account1",
                "account2",
                "reference1",
                1.23);
        final String result = workflow.transfer(transferRequest);

        Assert.assertEquals(result, "some-transference-id");

        /////////////

    }



}