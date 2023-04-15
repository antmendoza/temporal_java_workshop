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

package io.temporal.step10.moneytransferapp;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.step10.moneytransferapp.workflow.MoneyTransferWorkflow;
import io.temporal.step10.moneytransferapp.workflow.TransferRequest;
import io.temporal.step10.moneytransferapp.workflow.TransferRequests;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.temporal.step10.moneytransferapp.workflow.MoneyTransferWorkflowImpl.TASK_QUEUE;


public class Starter {

    private static final String MY_BUSINESS_ID = "money-transfer";

    public static void main(String[] args) {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        final WorkflowClient client = WorkflowClient.newInstance(service);


        // Create the workflow client stub. It is used to start our workflow execution.
        final WorkflowOptions build = WorkflowOptions.newBuilder()
                .setWorkflowId(MY_BUSINESS_ID)
                .setTaskQueue(TASK_QUEUE)
                .build();


        final MoneyTransferWorkflow workflow =
                client.newWorkflowStub(
                        MoneyTransferWorkflow.class,
                        build);


        final List request = IntStream.rangeClosed(1, 100)
                .mapToObj(i ->
                        new TransferRequest("accountFrom" + i,
                                "accountTo" + i,
                                "referenceId" + i,
                                200 + i))
                .collect(Collectors.toList());


        workflow.transfer(new TransferRequests(request));


    }

}
