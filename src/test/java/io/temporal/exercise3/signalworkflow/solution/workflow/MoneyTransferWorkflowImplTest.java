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

package io.temporal.exercise3.signalworkflow.solution.workflow;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.temporal.DescribeWorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.exercise3.signalworkflow.solution.workflow.activity.NotificationService;
import io.temporal.exercise3.signalworkflow.solution.workflow.activity.NotificationServiceImpl;
import io.temporal.model.TransferRequest;
import io.temporal.service.AccountService;
import io.temporal.service.AccountServiceImpl;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class MoneyTransferWorkflowImplTest {

  private final String myWorkflowId = "myWorkflow";

  @Rule
  public TestWorkflowRule testWorkflowRule =
      TestWorkflowRule.newBuilder().setDoNotStart(true).build();

  @After
  public void shutdown() {
    testWorkflowRule.getTestEnvironment().shutdown();
  }

  @Test
  public void testTransferAuthorizationNotRequired() {

    AccountService accountService = Mockito.mock(AccountServiceImpl.class);
    NotificationService notificationService = Mockito.mock(NotificationServiceImpl.class);

    Worker worker = testWorkflowRule.getWorker();
    worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
    worker.registerActivitiesImplementations(accountService, notificationService);

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

    TransferRequest transferRequest =
        new TransferRequest("fromAccount", "toAccount", "reference1", 1.23);

    // Start workflow sync
    workflow.transfer(transferRequest);

    DescribeWorkflowExecutionResponse describeResponse =
        new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule).get();

    Assert.assertEquals(
        describeResponse.getWorkflowExecutionInfo().getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

    // Assert notificationService has been invoked
    verify(notificationService, times(1)).notifyCustomerTransferDone();
    /////////////

  }

  @Test
  public void testApproveTransfer() {

    AccountService accountService = Mockito.mock(AccountServiceImpl.class);
    NotificationService notificationService = Mockito.mock(NotificationServiceImpl.class);

    Worker worker = testWorkflowRule.getWorker();
    worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
    worker.registerActivitiesImplementations(accountService, notificationService);

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

    TransferRequest transferRequest =
        new TransferRequest("fromAccount", "toAccount", "reference1", 1230);

    // Start async
    WorkflowClient.start(workflow::transfer, transferRequest);

    Assert.assertEquals(
        new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule)
            .get()
            .getWorkflowExecutionInfo()
            .getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING);

    // Approve transfer
    workflow.approveTransfer(TRANSFER_APPROVED.YES);

    // Wait workflow to complete
    workflowClient.newUntypedWorkflowStub(myWorkflowId).getResult(Void.class);

    Assert.assertEquals(
        new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule)
            .get()
            .getWorkflowExecutionInfo()
            .getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

    // Assert notificationService has been invoked
    verify(notificationService, times(1)).notifyCustomerTransferDone();

    /////////////

  }

  @Test
  public void testDenyTransfer() {

    NotificationService notificationService = Mockito.mock(NotificationServiceImpl.class);

    Worker worker = testWorkflowRule.getWorker();
    worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
    worker.registerActivitiesImplementations(notificationService);

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

    TransferRequest transferRequest =
        new TransferRequest("fromAccount", "toAccount", "reference1", 1230);

    // Start async
    WorkflowClient.start(workflow::transfer, transferRequest);

    Assert.assertEquals(
        new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule)
            .get()
            .getWorkflowExecutionInfo()
            .getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING);

    // Approve transfer
    workflow.approveTransfer(TRANSFER_APPROVED.NO);

    // Wait workflow to complete
    workflowClient.newUntypedWorkflowStub(myWorkflowId).getResult(Void.class);

    Assert.assertEquals(
        new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule)
            .get()
            .getWorkflowExecutionInfo()
            .getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

    // Assert notificationService has been invoked
    verify(notificationService, times(1)).notifyCustomerTransferNotApproved();

    /////////////

  }
}
