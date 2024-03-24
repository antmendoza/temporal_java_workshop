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
    private final List<Transfer> requests = new ArrayList<>();
    private Account account;
    private boolean closeAccount = false;
    private List<Operation> operations = new ArrayList<>();


    @Override
    public void open(final Account account) {
        this.account = account;
        this.operations = new ArrayList<>();

        while (!closeAccount) {

            Workflow.await(() -> !requests.isEmpty() || closeAccount);

            if (!requests.isEmpty()) {
                final Transfer transfer = requests.remove(0);
                final String targetCustomer = transfer.targetCustomer();

                final MoneyTransferWorkflow child = Workflow.newChildWorkflowStub(MoneyTransferWorkflow.class);


                final TransferRequest transferRequest = new TransferRequest(
                        this.account.accountId(),
                        targetCustomer,
                        transfer.requestId(),
                        transfer.amount());


                //Start and wait for the child workflow to complete
                var childRequestResponse = child.transfer(transferRequest);


                if (childRequestResponse.isApproved()) {
                    this.account = this.account.subtract(transfer.amount());
                    this.operations.add(new Operation(childRequestResponse));
                }
            }
        }


        final AccountCleanUpWorkflow accountCleanUpWorkflow = Workflow.newChildWorkflowStub(AccountCleanUpWorkflow.class,
                ChildWorkflowOptions
                        .newBuilder()
                        .setWorkflowId(AccountCleanUpWorkflow.workflowIdFromAccountId(account.accountId()))
                        .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                        .build());


        Async.procedure(accountCleanUpWorkflow::run, this.account);
        Promise<WorkflowExecution> childExecution = Workflow.getWorkflowExecution(accountCleanUpWorkflow);

        // Wait for child to start https://community.temporal.io/t/best-way-to-create-an-async-child-workflow/114/2
        // By exiting here we are closing the current workflow
        // AccountCleanUpWorkflow will continue running due to PARENT_CLOSE_POLICY_ABANDON
        // More info for PARENT_CLOSE_POLICY https://docs.temporal.io/workflows#parent-close-policy
        childExecution.get();

    }

    @Override
    public void requestTransfer(final Transfer transferRequest) {
        this.requests.add(transferRequest);
    }

    @Override
    public UpdateCustomerResponse updateCustomer(final String newCustomerIdValue) {

        log.debug("updating customer: " + newCustomerIdValue);
        final UpdateCustomerResponse updateCustomerResponse =
                new UpdateCustomerResponse(this.account.customerId(), newCustomerIdValue);
        this.account = new Account(this.account.accountId(), newCustomerIdValue, this.account.amount());
        return updateCustomerResponse;

    }


    @Override
    public CloseAccountResponse closeAccount() {
        this.closeAccount = true;
        return new CloseAccountResponse(account);
    }


    @Override
    public List<List<Operation>> getOperations() {
        return List.of(this.operations);
    }

    @Override
    public Account getAccount() {
        return this.account;
    }

}



