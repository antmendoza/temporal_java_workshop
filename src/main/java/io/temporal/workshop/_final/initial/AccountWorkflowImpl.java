package io.temporal.workshop._final.initial;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import io.temporal.workshop._final.AccountCleanUpWorkflow;
import io.temporal.workshop._final.AccountWorkflow;
import io.temporal.workshop._final.MoneyTransferWorkflow;
import io.temporal.workshop.model.*;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountWorkflowImpl implements
        AccountWorkflow {
    private final Logger log = Workflow.getLogger(AccountWorkflowImpl.class.getSimpleName());
    private final List<TransferRequest> pendingRequest = new ArrayList<>();
    private List<Operation> operations = new ArrayList<>();
    private Account account;
    private boolean closeAccount = false;
    private final Map<TransferRequest, WorkflowExecution> map = new HashMap<>();

    @Override
    public void open(final Account account) {

        log.info("Account created " + account);

    }

    @Override
    public RequestTransferResponse requestTransfer(final TransferRequest transferRequest) {
        return null;
    }

    @Override
    public void validateCloseAccount() {

    }

    @Override
    public CloseAccountResponse closeAccount() {
        return null;
    }

    @Override
    public AccountSummaryResponse getAccountSummary() {
        return new AccountSummaryResponse(account, operations);
    }

    @Override
    public void withdraw(final double amount) {
        this.account = this.account.withdraw(amount);
    }

    @Override
    public void deposit(final double amount) {
        this.account = this.account.deposit(amount);
    }

}



