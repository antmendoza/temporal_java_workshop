package io.temporal._final.solution.workflow;

import io.temporal.model.*;
import io.temporal.workflow.*;

import java.util.List;

@WorkflowInterface
public interface AccountWorkflow {

    static String workflowIdFromAccountId(String value) {
        return "account[" + value + "]";
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

}

