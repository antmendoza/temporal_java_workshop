package io.temporal.exercise0.firstworkflow.initial;

import io.temporal.model.TransferRequest;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    @Override
    public String transfer(TransferRequest transferRequest) {

        System.out.println("My first workflow " + transferRequest);

        return "done";
    }

}