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

package io.temporal.exercise10.alltogether.solution.parallel;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.exercise10.alltogether.solution.parallel.workflow.MoneyTransferWorkflow;
import io.temporal.model.TransferRequest;
import io.temporal.model.TransferRequests;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Starter {

  static final String MY_BUSINESS_ID = Starter.class.getPackageName() + ":money-transfer";

  public static void main(String[] args) {

    int numRequest = 100;
    startTransfer(numRequest);
  }

  public static void startTransfer(int numRequest) {

    // Get a Workflow service stub.
    final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    final WorkflowClient client = WorkflowClient.newInstance(service);

    // Create the workflow client stub. It is used to start our workflow execution.
    final WorkflowOptions build =
        WorkflowOptions.newBuilder()
            .setWorkflowId(MY_BUSINESS_ID)
            .setTaskQueue(WorkerProcess.TASK_QUEUE)
            .build();

    final MoneyTransferWorkflow workflow =
        client.newWorkflowStub(MoneyTransferWorkflow.class, build);

    final List request =
        IntStream.range(0, numRequest)
            .mapToObj(
                i ->
                    new TransferRequest(
                        "fromAccount-" + i, "toAccount-" + i, "referenceId-" + i, 200 + i))
            .collect(Collectors.toList());

    workflow.transfer(new TransferRequests(request));
  }
}