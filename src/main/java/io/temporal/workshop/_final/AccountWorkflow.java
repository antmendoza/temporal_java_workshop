package io.temporal.workshop._final;

import io.temporal.workflow.*;
import io.temporal.workshop.model.*;

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
    void withdraw(double amount);

    @UpdateMethod
    void deposit(double amount);


    @UpdateMethod
    CloseAccountResponse closeAccount();

    @QueryMethod
    AccountSummaryResponse getAccountSummary();


}

