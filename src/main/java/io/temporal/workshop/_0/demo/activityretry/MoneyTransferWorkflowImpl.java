package io.temporal.workshop._0.demo.activityretry;

import io.temporal.workshop.activity.AccountService;
import io.temporal.workshop.activity.NotificationService;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.service.DepositRequest;
import io.temporal.workshop.service.WithdrawRequest;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
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
                                            //after max attempts is reached the activity will be marked as failed
                                            //.setMaximumAttempts(5)
                                            .setInitialInterval(Duration.ofSeconds(5))
                                            .setMaximumInterval(Duration.ofSeconds(5))
                                            .build())
                            .build());
    private final NotificationService notificationService =
            Workflow.newActivityStub(
                    NotificationService.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(3))
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


        notificationService.transferCompleted(transferRequest);
        log.info("Operation completed: " + transferRequest);
    }
}
