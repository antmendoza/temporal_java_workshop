package io.temporal.workshop._final.solution;

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
    private final List<TransferRequest> pendingTransferRequests = new ArrayList<>();
    private final Map<TransferRequest, String> startedRequest = new HashMap<>();
    private List<Operation> operations = new ArrayList<>();
    private Account account;
    private boolean closeAccount = false;

    @Override
    public void open(final Account account) {

        log.info("Account created " + account);
        this.account = account;
        this.operations = new ArrayList<>();


        //while the workflow is open
        while (!closeAccount) {


            //Block until a new transfer request comes, or the request to close the account
            Workflow.await(() -> !pendingTransferRequests.isEmpty() || closeAccount);


            //If there is a pending request
            if (!pendingTransferRequests.isEmpty()) {


                //Process it starting the MoneyTransferWorkflow
                // (as [ChildWorkflow](https://docs.temporal.io/dev-guide/java/features#child-workflows)) asynchronously.
                final TransferRequest transferRequest = pendingTransferRequests.get(0);

                final String moneyTransferWorkflowId = "money-transfer-FROM_" + transferRequest.fromAccountId() +
                        "_TO_" + transferRequest.toAccountId()+"_AMOUNT_" + transferRequest.amount();

                log.info("Scheduling workflow " + moneyTransferWorkflowId);

                final MoneyTransferWorkflow moneyTransferWorkflow =
                        Workflow.newChildWorkflowStub(MoneyTransferWorkflow.class,
                                ChildWorkflowOptions.newBuilder()
                                        .setWorkflowId(
                                                moneyTransferWorkflowId)
                                        .build());

                // We start the ChildWorkflow asynchronously, we don't want to block the user request until the child workflow completes.
                // https://community.temporal.io/t/best-way-to-create-an-async-child-workflow/114/2
                final Promise<TransferResponse> request = Async.function(moneyTransferWorkflow::transfer, transferRequest);
                WorkflowExecution execution = Workflow.getWorkflowExecution(moneyTransferWorkflow).get();


                // Remove the request from pendingTransfer after the workflow start
                pendingTransferRequests.remove(transferRequest);

                // Unblock #1
                // This is the workflowId we return to the client as response to `requestTransfer`
                startedRequest.put(transferRequest, execution.getWorkflowId());

                request.thenApply((response)->{

                    // wait until the ChildWorkflow completes and add the result to the list or operations to track it.
                    //This list is returned as part of `getAccountSummary`
                    final TransferResponse transferResponse = request.get();
                    this.operations.add(new Operation(execution.getWorkflowId(), transferResponse));

                   return response;
               });


            }
        }


    }

    @Override
    public RequestTransferResponse requestTransfer(final TransferRequest transferRequest) {

        //add the operation to `pendingTransferRequests`. We will process each request in the main workflow thread.
        this.pendingTransferRequests.add(transferRequest);

        //#1 wait until the operation starts, to get the workflowId or the moneyTransfer workflow
        Workflow.await(() -> startedRequest.get(transferRequest) != null);

        //Return workflowId
        final String operationId = startedRequest.get(transferRequest);
        return new RequestTransferResponse(operationId);

    }


    @Override
    public CloseAccountResponse closeAccount() {
        this.closeAccount = true;
        return new CloseAccountResponse(account);
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



