package io.temporal.exercise10.alltogether.solution.workflow;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.exercise10.alltogether.solution.workflow.child.AccountCleanUpWorkflow;
import io.temporal.exercise10.alltogether.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.model.TransferRequest;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountWorkflowImpl implements
        AccountWorkflow {
    private final Logger log = Workflow.getLogger(AccountWorkflowImpl.class.getSimpleName());


    private Account account;
    private boolean closeAccount = false;
    private final List<Transfer> requests = new ArrayList<>();

    @Override
    public void open(final Account account) {
        this.account = account;

        while (!closeAccount) {

            Workflow.await(() -> !requests.isEmpty() || closeAccount);

            if (!requests.isEmpty()) {
                final Transfer transfer = requests.remove(0);
                final String targetCustomer = transfer.targetCustomer();

                final MoneyTransferWorkflow child = Workflow.newChildWorkflowStub(MoneyTransferWorkflow.class);

                try{

                final String reference = child.transfer(new TransferRequest(
                        this.account.accountId(),
                        targetCustomer,
                        transfer.requestId(),
                        transfer.amount()));

                this.account = this.account.subtract(transfer.amount());

                }catch (Exception e){
                    //TODO log not enough money
                    //TODO send notification to customer
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
        childExecution.get();


        //By exiting here we are closing the current workflow
        //AccountCleanUpWorkflow will continue running due to PARENT_CLOSE_POLICY_ABANDON
        // More info for PARENT_CLOSE_POLICY https://docs.temporal.io/workflows#parent-close-policy

    }

    @Override
    public void requestTransfer(final Transfer transferRequest) {
        this.requests.add(transferRequest);
    }


    @Override
    public void validateUpdateCustomer(final String newCustomerIdValue) {
        if (!Objects.equals(this.account.customerId(), newCustomerIdValue)) {
            throw new RuntimeException("Customer id do not match : " + this.account.customerId() + "!=" + newCustomerIdValue);
        }
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
    public String closeAccount() {

        log.debug("Close account");

        this.closeAccount = true;
        return "RequestId[" + Math.random() + "]";
    }


}
