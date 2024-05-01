package io.temporal.workshop._final.solution.workflow;

import io.temporal.workshop.model.*;
import io.temporal.workshop._final.solution.workflow.child.AccountCleanUpWorkflow;
import io.temporal.workshop._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountWorkflowImpl implements
        AccountWorkflow {
    private final Logger log = Workflow.getLogger(AccountWorkflowImpl.class.getSimpleName());
    private final List<TransferRequest> pendingRequest = new ArrayList<>();
    private List<Operation> operations = new ArrayList<>();
    private Account account;
    private boolean closeAccount = false;
    private final Map<TransferRequest, WorkflowExecution> map = new HashMap<TransferRequest, WorkflowExecution>();

    @Override
    public void open(final Account account) {

        log.info("Account created " + account);
        this.account = account;
        this.operations = new ArrayList<>();

        while (!closeAccount) {

            Workflow.await(() -> !pendingRequest.isEmpty() || closeAccount);

            if (!pendingRequest.isEmpty()) {

                final TransferRequest transferRequest = pendingRequest.get(0);

                final String moneyTransferWorkflowId = "money-transfer-FROM_" + transferRequest.fromAccountId() +
                        "_TO_" + transferRequest.toAccountId() +
                        // Why we use Workflow.currentTimeMillis()?
                        // https://docs.temporal.io/dev-guide/java/durable-execution#intrinsic-non-deterministic-logic
                        "_" + Workflow.currentTimeMillis();

                log.info("Scheduling workflow " + moneyTransferWorkflowId);

                final MoneyTransferWorkflow moneyTransferWorkflow =
                        Workflow.newChildWorkflowStub(MoneyTransferWorkflow.class,
                                ChildWorkflowOptions.newBuilder()
                                        .setWorkflowId(
                                                moneyTransferWorkflowId)
                                        .build());

                // Starting child workflow async
                // Wait for child to start
                // https://community.temporal.io/t/best-way-to-create-an-async-child-workflow/114/2
                final Promise<TransferResponse> request = Async.function(moneyTransferWorkflow::transfer, transferRequest);
                WorkflowExecution execution = Workflow.getWorkflowExecution(moneyTransferWorkflow).get();


                //#2
                pendingRequest.remove(transferRequest);

                //Unblock #1 in method requestTransfer
                map.put(transferRequest, execution);

                // wait for the child to complete
                final TransferResponse transferResponse = request.get();
                this.operations.add(new Operation(execution.getWorkflowId(),transferResponse));

            }
        }


        // Closing account
        // Start AccountCleanUpWorkflow that will be responsible for sending a notification to the customer,
        // among other things...
        final AccountCleanUpWorkflow accountCleanUpWorkflow = Workflow.newChildWorkflowStub(AccountCleanUpWorkflow.class,
                ChildWorkflowOptions
                        .newBuilder()
                        .setWorkflowId(AccountCleanUpWorkflow.workflowIdFromAccountId(account.accountId()))
                        // AccountCleanUpWorkflow will continue running due to PARENT_CLOSE_POLICY_ABANDON
                        // More info PARENT_CLOSE_POLICY https://docs.temporal.io/workflows#parent-close-policy
                        .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                        .build());


        Async.procedure(accountCleanUpWorkflow::run, this.account);
        // Wait for child to start https://community.temporal.io/t/best-way-to-create-an-async-child-workflow/114/2
        Workflow.getWorkflowExecution(accountCleanUpWorkflow).get();

        // By exiting here we are closing the current workflow execution
        //TODO add return
    }

    @Override
    public RequestTransferResponse requestTransfer(final TransferRequest transferRequest) {

        this.pendingRequest.add(transferRequest);

        //#1
        Workflow.await(() -> map.get(transferRequest) != null);

        //Return workflowId
        final String operationId = map.get(transferRequest).getWorkflowId();
        return new RequestTransferResponse(operationId);

    }

    @Override
    public void validateCloseAccount() {
        //#2
        if(!pendingRequest.isEmpty()){
            throw new RuntimeException("Account can't be closed, there are transfer requests in progress");
        }
    }

    @Override
    public CloseAccountResponse closeAccount() {
        this.closeAccount = true;
        return new CloseAccountResponse(account);
    }

    @Override
    public AccountSummaryResponse getAccountSummary() {
        return new AccountSummaryResponse(account, operations);
    }

    @Override
    public void withdraw(final double amount) {
        this.account = this.account.withdraw(amount);
    }

    @Override
    public void deposit(final double amount) {
        this.account = this.account.deposit(amount);

    }

}



