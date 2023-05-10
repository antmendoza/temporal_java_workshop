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

package io.temporal.exercise3.signalworkflow.solution;

import io.temporal.client.WorkflowClient;
import io.temporal.exercise3.signalworkflow.solution.workflow.MoneyTransferWorkflow;
import io.temporal.exercise3.signalworkflow.solution.workflow.TRANSFER_APPROVED;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.util.Optional;

public class SignalWorkflow {

  public static void main(String[] args) {
    signalWorkflow(TRANSFER_APPROVED.YES);
  }

  public static void signalWorkflow(TRANSFER_APPROVED yes) {
    // Get a Workflow service stub.
    final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    final WorkflowClient client = WorkflowClient.newInstance(service);

    final MoneyTransferWorkflow workflowStub =
        client.newWorkflowStub(
            MoneyTransferWorkflow.class, Starter.MY_BUSINESS_ID, Optional.empty());

    workflowStub.approveTransfer(yes);

    // newUntypedWorkflowStub
    // TODO
  }
}
