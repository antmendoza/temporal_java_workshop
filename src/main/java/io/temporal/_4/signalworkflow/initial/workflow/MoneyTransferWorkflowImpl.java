package io.temporal._4.signalworkflow.initial.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
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
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(3))
                            .setRetryOptions(
                                    RetryOptions.newBuilder()
                                            .setBackoffCoefficient(1)
                                            .setMaximumAttempts(5)
                                            .setMaximumInterval(Duration.ofSeconds(5))
                                            .build())
                            .build());

    private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());

    @Override
    public void transfer(TransferRequest transferRequest) {
        log.info("Init transfer: " + transferRequest);

        accountService.withdraw(
                new WithdrawRequest(
                        transferRequest.fromAccountId(),
                        transferRequest.amount()));
        accountService.deposit(
                new DepositRequest(
                        transferRequest.toAccountId(),
                        transferRequest.amount()));

        log.info("End transfer: " + transferRequest);
    }
}
