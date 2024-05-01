package io.temporal.workshop._final.initial.workflow.child;

import io.temporal.workshop.activity.AccountService;
import io.temporal.workshop.activity.NotificationService;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferResponse;
import io.temporal.workshop.model.TransferStatus;
import io.temporal.workshop.service.DepositRequest;
import io.temporal.workshop.service.WithdrawRequest;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.SearchAttributeKey;
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
    private final SearchAttributeKey<String> TransferRequestStatus = SearchAttributeKey.forKeyword("TransferRequestStatus");
    private TransferStatus transferStatus;
    private TransferRequest transferRequest;

    @Override
    public TransferResponse transfer(final TransferRequest transferRequest) {

        log.info("Init for transfer: " + transferRequest);

        this.transferRequest = transferRequest;

        this.transferStatus = TransferStatus.ApprovalNotRequired;

        if (transferRequest.amount() > 100) {

            transferStatus = TransferStatus.ApprovalRequired;

            //Setting this SA will allow query workflows by `TransferRequestStatus="ApprovalRequired"`
            //http://localhost:8080/namespaces/default/workflows?query=WorkflowType%3D%22MoneyTransferWorkflow%22+and+ExecutionStatus%3D%22Running%22+and+TransferRequestStatus%3D%22ApprovalRequired%22
            Workflow.upsertTypedSearchAttributes(
                    TransferRequestStatus.valueSet(transferStatus.name())
            );

            log.info("Request need approval: " + transferRequest);

            // Wait until status != TransferStatus.ApprovalRequired  or timeout
            final Duration timeout = Duration.ofSeconds(30); // Can be days, years...
            boolean authorizationReceivedWithinTimeOut =
                    Workflow.await(timeout, () ->
                    transferStatus != TransferStatus.ApprovalRequired);

            if (!authorizationReceivedWithinTimeOut) {
                transferStatus = TransferStatus.TimedOut;
                log.info("Authorization not received within " + timeout);
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

        //TODO add implementation to
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
        return new TransferResponse(transferRequest, transferStatus);
    }

    @Override
    public void changeTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    @Override
    public TransferRequest getTransferRequest() {
        return this.transferRequest;
    }



}
