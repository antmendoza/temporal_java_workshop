package io.temporal.workshop._3.query.solution;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import io.temporal.workshop.activity.AccountService;
import io.temporal.workshop.activity.NotificationService;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferResponse;
import io.temporal.workshop.model.TransferStatus;
import io.temporal.workshop.service.DepositRequest;
import io.temporal.workshop.service.WithdrawRequest;
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
                    ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());
    private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());
    private TransferStatus transferStatus;

    @Override
    public TransferResponse transfer(final TransferRequest transferRequest) {

        log.info("Init for request: " + transferRequest);

        transferStatus = TransferStatus.Approved;

        if (transferRequest.amount() >= 100) {
            transferStatus = TransferStatus.ApprovalRequired;

            final Duration timeout = Duration.ofSeconds(5); // Can be days, years...
            boolean transferStatusUpdatedWithinTimeOut =
                    Workflow.await(timeout, () ->
                            transferStatus != TransferStatus.ApprovalRequired);

            if (!transferStatusUpdatedWithinTimeOut) {
                transferStatus = TransferStatus.TimedOut;
                log.info("Status not updated within " + timeout+ " seconds");
                return new TransferResponse(transferRequest, transferStatus);
            }

        }


        if (TransferStatus.Approved != this.transferStatus) {
            log.info("Completed for request: " + transferRequest);
            final TransferResponse transferResponse = new TransferResponse(transferRequest, transferStatus);
            log.info("TransferResponse: " + transferResponse);
            return transferResponse;
        }

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
        final TransferResponse transferResponse = new TransferResponse(transferRequest, transferStatus);
        log.info("TransferResponse: " + transferResponse);
        return transferResponse;
    }

    @Override
    public TransferStatus getStatus() {
        return this.transferStatus;
    }




}
