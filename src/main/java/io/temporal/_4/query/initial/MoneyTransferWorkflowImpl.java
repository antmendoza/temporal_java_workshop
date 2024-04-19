package io.temporal._4.query.initial;

import io.temporal.activity.AccountService;
import io.temporal.activity.ActivityOptions;
import io.temporal.activity.NotificationService;
import io.temporal.model.TransferRequest;
import io.temporal.model.TransferResponse;
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

        log.info("Init for transfer: " + transferRequest);

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

        log.info("Completed for transfer: " + transferRequest);
    }


    @Override
    public TransferRequest getTransferRequest() {
        return this.transferRequest;
    }



}
