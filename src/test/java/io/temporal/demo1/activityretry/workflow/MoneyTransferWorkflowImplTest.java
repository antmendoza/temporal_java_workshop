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

package io.temporal.demo1.activityretry.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import io.temporal.activity.Activity;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.demo1.activityretry.workflow.activity.AccountService;
import io.temporal.demo1.activityretry.workflow.activity.AccountServiceImpl;
import io.temporal.model.TransferRequest;
import io.temporal.services.DepositRequest;
import io.temporal.services.WithdrawRequest;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class MoneyTransferWorkflowImplTest {

  private final String myWorkflowId = "myWorkflow";

  @Rule
  public TestWorkflowRule testWorkflowRule =
      TestWorkflowRule.newBuilder().setDoNotStart(true).build();

  @Test
  public void testTransfer() {

    AccountService accountService = Mockito.mock(AccountServiceImpl.class);

    Worker worker = testWorkflowRule.getWorker();
    worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
    worker.registerActivitiesImplementations(accountService);

    // Start server
    testWorkflowRule.getTestEnvironment().start();

    final WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setWorkflowId(myWorkflowId)
            .setTaskQueue(testWorkflowRule.getTaskQueue())
            .build();

    final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

    ///////////

    final MoneyTransferWorkflow workflow =
        workflowClient.newWorkflowStub(MoneyTransferWorkflow.class, options);

    // Start workflow
    TransferRequest transferRequest =
        new TransferRequest("fromAccount", "toAccount", "reference1", 1.23);

    workflow.transfer(transferRequest);

    DescribeWorkflowExecutionResponse describeResponse =
        describeWorkflowExecutionResponse(
            WorkflowExecution.newBuilder().setWorkflowId(myWorkflowId));

    Assert.assertEquals(
        describeResponse.getWorkflowExecutionInfo().getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

    /////////////

  }

  @Test
  public void testRetryAndSuccess() {

    AccountService accountService = Mockito.mock(AccountServiceImpl.class);
    doThrow(RuntimeException.class).when(accountService).deposit(any());

    Worker worker = testWorkflowRule.getWorker();
    worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
    worker.registerActivitiesImplementations(
        new AccountService() {
          @Override
          public void deposit(DepositRequest depositRequest) {
            int attends = Activity.getExecutionContext().getInfo().getAttempt();
            if (attends <= 2) {
              throw new NullPointerException("something is null");
            }
          }

          @Override
          public void withdraw(WithdrawRequest withdrawRequest) {}
        });

    // Start server
    testWorkflowRule.getTestEnvironment().start();

    final WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(testWorkflowRule.getTaskQueue())
            .setWorkflowId(myWorkflowId)
            .build();

    final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

    ///////////

    final MoneyTransferWorkflow workflow =
        workflowClient.newWorkflowStub(MoneyTransferWorkflow.class, options);

    // Start workflow
    TransferRequest transferRequest =
        new TransferRequest("fromAccount", "toAccount", "reference1", 1.23);

    workflow.transfer(transferRequest);

    DescribeWorkflowExecutionResponse describeResponse =
        describeWorkflowExecutionResponse(
            WorkflowExecution.newBuilder().setWorkflowId(myWorkflowId));

    Assert.assertEquals(
        describeResponse.getWorkflowExecutionInfo().getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

    /////////////

  }

  private DescribeWorkflowExecutionResponse describeWorkflowExecutionResponse(
      WorkflowExecution.Builder myWorkflow) {
    String namespace = testWorkflowRule.getTestEnvironment().getNamespace();
    DescribeWorkflowExecutionResponse describeResponse =
        testWorkflowRule
            .getTestEnvironment()
            .getWorkflowClient()
            .getWorkflowServiceStubs()
            .blockingStub()
            .describeWorkflowExecution(
                DescribeWorkflowExecutionRequest.newBuilder()
                    .setNamespace(namespace)
                    .setExecution(myWorkflow.build())
                    .build());
    return describeResponse;
  }
}
