package io.temporal._final.solution.workflow;

import io.temporal.model.*;
import io.temporal.workflow.*;

@WorkflowInterface
public interface AccountWorkflow {

    static String workflowIdFromAccountId(String accountId) {
        return "accountId_" + accountId;
    }

    @WorkflowMethod
    void open(Account account);

    @UpdateMethod
    RequestTransferResponse requestTransfer(TransferRequest transferRequest);

    @UpdateMethod
    CloseAccountResponse closeAccount();

    @QueryMethod
    AccountSummaryResponse getAccountSummary();


}

