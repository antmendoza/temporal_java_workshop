package io.temporal.workshop._3.initial;

import io.temporal.workshop.activity.AccountService;
import io.temporal.workshop.activity.NotificationService;
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

    private final NotificationService notificationService =
            Workflow.newActivityStub(
                    NotificationService.class,
                    ActivityOptions.newBuilder()
                            .setStartToCloseTimeout(Duration.ofSeconds(3))
                            .build());
    private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());
    private TransferRequest transferRequest;

    @Override
    public void transfer(final TransferRequest transferRequest) {

        log.info("Init for request: " + transferRequest);

        this.transferRequest = transferRequest;

        accountService.withdraw(
                new WithdrawRequest(
                        transferRequest.fromAccountId(),
                        transferRequest.amount()));

        accountService.deposit(
                new DepositRequest(
                        transferRequest.toAccountId(),
                        transferRequest.amount()));

        notificationService.transferCompleted(transferRequest);

        log.info("Completed for request: " + transferRequest);
    }


    @Override
    public TransferRequest getTransferRequest() {
        return this.transferRequest;
    }



}
