package io.temporal._final.solution.workflow;

import io.temporal.model.*;
import io.temporal.workflow.*;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;

import java.util.List;

@WorkflowInterface
public interface AccountWorkflow {

    static String workflowIdFromAccountId(String accountId) {
        return "account[" + accountId + "]";
    }

    @WorkflowMethod
    void open(Account account);

    @SignalMethod
    void requestTransfer(Transfer transferRequest);

    @UpdateMethod
    UpdateCustomerResponse updateCustomer(String newCustomerIdValue);

    @UpdateMethod
    CloseAccountResponse closeAccount();

    @QueryMethod
    List<List<Operation>> getOperations();


    @QueryMethod
    Account getAccount();



}

