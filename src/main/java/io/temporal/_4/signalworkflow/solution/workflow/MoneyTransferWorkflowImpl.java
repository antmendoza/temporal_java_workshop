package io.temporal._4.signalworkflow.solution.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal._4.signalworkflow.solution.workflow.activity.NotificationService;
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
    private TRANSFER_APPROVED transferApproved;

    @Override
    public void transfer(TransferRequest transferRequest) {

        log.info("Init transfer: " + transferRequest);

        if (transferRequest.amount() > 1000) {

            log.info("request need approval: " + transferRequest);

            Workflow.await(() -> transferApproved != null);
            // comment the line above and uncomment next block. Stop worker, start workflow, start
            // worker and wait 2 seconds without signaling the workflow execution

      /*  Duration timeout = Duration.ofSeconds(2); // Can be days, years...
      boolean authorizationReceived = Workflow.await(timeout, () -> transferApproved != null);
      if (!authorizationReceived) {
        log.info("authorization not received within " + timeout);
        return;
      }*/

            ///

            log.info("transferApproved: " + transferApproved);

            if (TRANSFER_APPROVED.NO.equals(transferApproved)) {
                // notify customer...
                notificationService.notifyCustomerTransferNotApproved();
                log.info("notify customer, transferApproved: " + transferRequest);
                return;
            }
        }

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

        notificationService.notifyCustomerTransferDone();

        log.info("End transfer: " + transferRequest);
    }

    @Override
    public void approveTransfer(TRANSFER_APPROVED transferApproved) {
        this.transferApproved = transferApproved;
    }
}
