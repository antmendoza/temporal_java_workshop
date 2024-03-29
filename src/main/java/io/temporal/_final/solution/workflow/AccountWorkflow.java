package io.temporal._final.solution.workflow;

import io.temporal.model.*;
import io.temporal.workflow.*;

import java.util.List;

@WorkflowInterface
public interface AccountWorkflow {

    static String workflowIdFromAccountId(String accountId) {
        return "accountId_" + accountId;
    }

    @WorkflowMethod
    void open(Account account);

    @SignalMethod
    void requestTransfer(TransferRequest transferRequest);

    @UpdateMethod
    CloseAccountResponse closeAccount();


    @UpdateMethod
    CloseAccountResponse updateCustomerName();


    @QueryMethod
    AccountSummaryResponse getAccountSummary();

    @QueryMethod
    Account getAccount();


}

