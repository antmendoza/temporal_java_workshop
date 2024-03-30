package io.temporal._final.solution.workflow;

import io.temporal._final.solution.workflow.child.AccountCleanUpWorkflow;
import io.temporal._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.model.*;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AccountWorkflowImpl implements
        AccountWorkflow {
    private final Logger log = Workflow.getLogger(AccountWorkflowImpl.class.getSimpleName());
    private final List<TransferRequest> pendingRequest = new ArrayList<>();
    private Account account;
    private boolean closeAccount = false;
    private List<Operation> operations = new ArrayList<>();


    @Override
    public void open(final Account account) {

        log.info("Account created " + account);
        this.account = account;
        this.operations = new ArrayList<>();

        while (!closeAccount) {

            Workflow.await(() -> !pendingRequest.isEmpty() || closeAccount);

            if (!pendingRequest.isEmpty()) {

                final TransferRequest transferRequest = pendingRequest.remove(0);

                final MoneyTransferWorkflow child =
                        Workflow.newChildWorkflowStub(MoneyTransferWorkflow.class,
                                ChildWorkflowOptions.newBuilder()
                                        .setWorkflowId(MoneyTransferWorkflow.createWorkflowId(transferRequest))
                                        .build());


                //Start and wait for the child workflow to complete
                var childRequestResponse = child.transfer(transferRequest);

                this.operations.add(new Operation(childRequestResponse));

                if (childRequestResponse.isApproved()) {
                    this.account = this.account.subtract(transferRequest.amount());
                }

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
        Promise<WorkflowExecution> childExecution = Workflow.getWorkflowExecution(accountCleanUpWorkflow);

        // Wait for child to start https://community.temporal.io/t/best-way-to-create-an-async-child-workflow/114/2
        childExecution.get();

        // By exiting here we are closing the current workflow
        //TODO add return
    }

    @Override
    public void requestTransfer(final TransferRequest transferRequest) {
        this.pendingRequest.add(transferRequest);
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


}



