package io.temporal.exercise10.alltogether.solution.workflow;

import io.temporal.exercise10.alltogether.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.model.TransferRequest;
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
    private List<Transfer> requests = new ArrayList<>();

    @Override
    public void open(final Account account) {
        this.account = account;

        while(!closeAccount){

            Workflow.await(() -> !requests.isEmpty() || closeAccount);

            if(!requests.isEmpty()){
                final Transfer transfer = requests.remove(0);
                final String targetCustomer = transfer.targetCustomer();

                final MoneyTransferWorkflow child =Workflow.newChildWorkflowStub(MoneyTransferWorkflow.class);

                final String reference = child.transfer(new TransferRequest(
                        this.account.accountId(),
                        targetCustomer,
                        transfer.requestId(),
                        transfer.amount()));
                System.out.println("reference " + reference);
            }
        }

    }

    @Override
    public void requestTransfer(final Transfer transferRequest) {
        this.requests.add(transferRequest);
    }




    @Override
    public void validateUpdateCustomer(final Customer newValue) {
        if (!Objects.equals(this.account.customer().customerId(), newValue.customerId())) {
            throw new RuntimeException("Customer id do not match : " + this.account.customer().customerId() + "!=" + newValue.customerId());
        }
    }


    @Override
    public UpdateCustomerResponse updateCustomer(final Customer customer) {

        log.debug("updating customer: " + customer);

        final UpdateCustomerResponse updateCustomerResponse =
                new UpdateCustomerResponse(this.account.customer(), customer);
        this.account = new Account(this.account.accountId(), customer, this.account.amount());
        return updateCustomerResponse;
    }

    @Override
    public void closeAccount() {

        log.debug("Close account" );

        this.closeAccount = true;
    }


}
