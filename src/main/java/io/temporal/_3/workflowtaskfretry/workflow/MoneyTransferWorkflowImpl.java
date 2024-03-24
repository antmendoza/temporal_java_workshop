package io.temporal._3.workflowtaskfretry.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.model.TransferRequest;
import io.temporal.service.AccountService;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    private final AccountService accountService =
            Workflow.newActivityStub(
                    AccountService.class,
                    ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());
    private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());

    @Override
    public void transfer(TransferRequest transferRequest) {
        log.info("Init transfer: " + transferRequest);

        double amount = transferRequest.amount();
        accountService.withdraw(
                new WithdrawRequest(
                        transferRequest.fromAccountId(), amount));

        double depositAmount = amount;
        if (amount > 20) {
            // The purpose is to demostrate how, in presence of a runtime error,
            // after fixing the code the workflow execution will continue from where the execution was
            // stopped

            // calculate fee
            // TODO fix the code and restart WorkerProcess
            // depositAmount = amount - amount * 0.1;
            depositAmount = amount - (int) amount / 0;
        }
        accountService.deposit(
                new DepositRequest(
                        transferRequest.toAccountId(), depositAmount));

        log.info("End transfer: " + transferRequest);
    }
}
