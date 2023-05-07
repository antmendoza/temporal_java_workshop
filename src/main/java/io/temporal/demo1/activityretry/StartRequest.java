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

package io.temporal.demo1.activityretry;

import static io.temporal.demo1.activityretry.WorkerProcess.TASK_QUEUE;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.demo1.activityretry.workflow.MoneyTransferWorkflow;
import io.temporal.model.TransferRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class StartRequest {

  static final String MY_BUSINESS_ID = StartRequest.class.getPackageName() + ":money-transfer";

  public static void main(String[] args) {

    // Get a Workflow service stub.
    final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    final WorkflowClient client = WorkflowClient.newInstance(service);

    final WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setWorkflowId(MY_BUSINESS_ID)
            .setTaskQueue(TASK_QUEUE)
            // .setWorkflowRunTimeout(Duration.ofDays(2))
            .build();

    // Create the workflow client stub.
    // It is used to start our workflow execution.
    final MoneyTransferWorkflow workflow =
        client.newWorkflowStub(MoneyTransferWorkflow.class, options);

    workflow.transfer(new TransferRequest("fromAccount", "toAccount", "referenceId", 200));
  }
}
