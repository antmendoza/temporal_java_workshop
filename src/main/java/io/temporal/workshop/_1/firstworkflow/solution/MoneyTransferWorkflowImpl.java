package io.temporal.workshop._1.firstworkflow.solution;

import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.service.DepositRequest;
import io.temporal.workshop.service.WithdrawRequest;
import io.temporal.activity.ActivityOptions;
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
    public String transfer(TransferRequest transferRequest) {
        log.info("Init transfer: " + transferRequest);

        accountService.withdraw(
                new WithdrawRequest(
                        transferRequest.fromAccountId(),
                        transferRequest.amount()));

        // Exception
        accountService.deposit(
                new DepositRequest(
                        transferRequest.toAccountId(),
                        transferRequest.amount()));

        log.info("End transfer: " + transferRequest);

        return "done";
    }
}
