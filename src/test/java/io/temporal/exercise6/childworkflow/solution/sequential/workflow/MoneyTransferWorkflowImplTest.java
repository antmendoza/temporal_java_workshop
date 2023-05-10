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

package io.temporal.exercise6.childworkflow.solution.sequential.workflow;

import io.temporal.DescribeWorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.api.workflowservice.v1.ListClosedWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.ListClosedWorkflowExecutionsResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.TransferRequest;
import io.temporal.model.TransferRequests;
import io.temporal.service.AccountService;
import io.temporal.service.AccountServiceImpl;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
  public void testTransfer() {

    AccountService accountService = Mockito.mock(AccountServiceImpl.class);

    Worker worker = testWorkflowRule.getWorker();
    worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
    worker.registerWorkflowImplementationTypes(MoneyTransferChildWorkflowImpl.class);
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

    final List request =
        IntStream.range(0, 10)
            .mapToObj(
                i ->
                    new TransferRequest(
                        "fromAccount-" + i, "toAccount-" + i, "referenceId-" + i, 200 + i))
            .collect(Collectors.toList());

    // Start async
    WorkflowClient.start(workflow::transfer, new TransferRequests(request));

    // Wait workflow to complete
    workflowClient.newUntypedWorkflowStub(myWorkflowId).getResult(Void.class);

    ListClosedWorkflowExecutionsResponse listClosedWorkflowExecutions =
        workflowClient
            .getWorkflowServiceStubs()
            .blockingStub()
            .listClosedWorkflowExecutions(ListClosedWorkflowExecutionsRequest.newBuilder().build());

    Assert.assertEquals(listClosedWorkflowExecutions.getExecutionsCount(), 11);

    DescribeWorkflowExecutionResponse describeResponse =
        new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule).get();

    Assert.assertEquals(
        describeResponse.getWorkflowExecutionInfo().getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

    /////////////

  }
}
