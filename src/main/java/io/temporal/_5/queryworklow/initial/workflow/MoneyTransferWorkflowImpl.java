package io.temporal._5.queryworklow.initial.workflow;

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
    private TRANSFER_APPROVED transferApproved;

    @Override
    public void transfer(TransferRequest transferRequest) {

        log.info("Init transfer: " + transferRequest);

        if (transferRequest.amount() > 1000) {
            log.info("request need approval: " + transferRequest);
            Workflow.await(() -> transferApproved != null);
            log.info("transferApproved: " + transferApproved);

            if (TRANSFER_APPROVED.NO.equals(transferApproved)) {
                // notify customer...
                log.info("notify customer, transferApproved: " + transferRequest);
                return;
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

        log.info("End transfer: " + transferRequest);
    }

    @Override
    public void approveTransfer(TRANSFER_APPROVED transferApproved) {
        this.transferApproved = transferApproved;
    }
}
