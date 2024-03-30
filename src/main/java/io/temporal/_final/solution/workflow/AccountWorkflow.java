package io.temporal._final.solution.workflow;

import io.temporal.model.Account;
import io.temporal.model.AccountSummaryResponse;
import io.temporal.model.CloseAccountResponse;
import io.temporal.model.TransferRequest;
import io.temporal.workflow.*;

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

    @QueryMethod
    AccountSummaryResponse getAccountSummary();


}

