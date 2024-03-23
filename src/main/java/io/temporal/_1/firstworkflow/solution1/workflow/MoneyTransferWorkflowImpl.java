package io.temporal._1.firstworkflow.solution1.workflow;

import io.temporal.model.TransferRequest;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    @Override
    public String transfer(TransferRequest transferRequest) {

        System.out.println("My first workflow " + transferRequest);

        return "done";
    }
}
