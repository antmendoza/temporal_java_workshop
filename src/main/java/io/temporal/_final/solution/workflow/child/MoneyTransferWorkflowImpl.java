package io.temporal._final.solution.workflow.child;

import io.temporal.activity.ActivityOptions;
import io.temporal._final.solution.workflow.activity.NotificationService;
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

    private final NotificationService notificationService =
            Workflow.newActivityStub(
                    NotificationService.class,
                    ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(3)).build());
    private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());
    private TransferStatus transferStatus;

    @Override
    public TransferResponse transfer(TransferRequest transferRequest) {

        log.info("Init transfer: " + transferRequest);

        if (transferRequest.amount() > 1000) {

            log.info("request need approval: " + transferRequest);


            // Wait until the signal is received or timeout reached
            final Duration timeout = Duration.ofSeconds(10); // Can be days, years...
            boolean authorizationReceivedWithinTimeOut = Workflow.await(timeout, () -> transferStatus != null);
            if (!authorizationReceivedWithinTimeOut) {
                transferStatus = TransferStatus.TimedOut;
                log.info("authorization not received within " + timeout);
                return new TransferResponse(transferRequest, transferStatus);
            }


            // Or we can just wait forever
            // Workflow.await(() -> transferStatus != null);


            log.info("transferApproved: " + transferStatus);

            if (TransferStatus.Denied.equals(transferStatus)) {
                // notify customer...
                notificationService.transferDenied(transferRequest);
                log.info("notify customer, transferApproved: " + transferRequest);
                return new TransferResponse(transferRequest, transferStatus);

            }
        }

        this.transferStatus = TransferStatus.Approved;
        accountService.withdraw(
                new WithdrawRequest(
                        transferRequest.fromAccountId(),
                        transferRequest.referenceId(),
                        transferRequest.amount()));
        accountService.deposit(
                new DepositRequest(
                        transferRequest.toAccountId(),
                        transferRequest.referenceId(),
                        transferRequest.amount()));

        notificationService.transferCompleted(transferRequest);

        log.info("End transfer: " + transferRequest);
        return new TransferResponse(transferRequest, transferStatus);
    }

    @Override
    public void approveTransfer(TransferStatus transferApproved) {
        this.transferStatus = transferApproved;
    }
}
