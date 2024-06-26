package io.temporal.workshop._final.initial;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.SearchAttributeKey;
import io.temporal.workflow.Workflow;
import io.temporal.workshop._final.MoneyTransferWorkflow;
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
    private final SearchAttributeKey<String> TransferRequestStatus = SearchAttributeKey.forKeyword("TransferRequestStatus");
    private TransferStatus transferStatus;
    private TransferRequest transferRequest;

    @Override
    public TransferResponse transfer(final TransferRequest transferRequest) {

        log.info("Init for request: " + transferRequest);

        this.transferRequest = transferRequest;

        this.transferStatus = TransferStatus.ApprovalNotRequired;

        if (transferRequest.amount() > 100) {

            transferStatus = TransferStatus.ApprovalRequired;

            //TODO add code here

            log.info("Request need approval: " + transferRequest);

            // Wait until status != TransferStatus.ApprovalRequired  or timeout
            final Duration timeout = Duration.ofSeconds(30); // Can be days, years...
            boolean statusUpdatedWithinTimeOut =
                    Workflow.await(timeout, () ->
                    transferStatus != TransferStatus.ApprovalRequired);

            if (!statusUpdatedWithinTimeOut) {
                transferStatus = TransferStatus.TimedOut;
                log.info("Status not updated within " + timeout+ " seconds");
                return new TransferResponse(transferRequest, transferStatus);
            }


            log.info("transferStatus: " + transferStatus);

            if (TransferStatus.Denied.equals(transferStatus)) {
                // notify customer...
                notificationService.operationDenied(transferRequest);
                log.info("Notify customer, transferRequest: " + transferRequest);
                return new TransferResponse(transferRequest, transferStatus);

            }
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
        return new TransferResponse(transferRequest, transferStatus);
    }

    @Override
    public void setTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    @Override
    public TransferRequest getTransferRequest() {
        return this.transferRequest;
    }



}
