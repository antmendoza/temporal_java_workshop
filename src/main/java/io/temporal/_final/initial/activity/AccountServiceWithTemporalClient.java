package io.temporal._final.initial.activity;

import io.temporal._final.initial.workflow.AccountWorkflow;
import io.temporal.activity.AccountService;
import io.temporal.client.WorkflowClient;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class AccountServiceWithTemporalClient implements AccountService {

    private final Logger log = Workflow.getLogger(AccountServiceWithTemporalClient.class.getSimpleName());
    private final WorkflowClient client;


    public AccountServiceWithTemporalClient(WorkflowClient client) {
        this.client = client;
    }

    @Override
    public void withdraw(WithdrawRequest withdrawRequest) {
        log.info("Init withdraw : " + withdrawRequest);
        final String workflowId =
                AccountWorkflow.workflowIdFromAccountId(withdrawRequest.accountId());
        client
                .newWorkflowStub(AccountWorkflow.class,
                        workflowId)
                //Update workflow, this is a blocking operations that will
                // return once the returns once deposit method has completed
                .withdraw(withdrawRequest.amount());

        log.info("End withdraw : " + withdrawRequest);
    }

    @Override
    public void deposit(DepositRequest depositRequest) {
        log.info("Init deposit : " + depositRequest);
        final String workflowId =
                AccountWorkflow.workflowIdFromAccountId(depositRequest.accountId());
        client
                .newWorkflowStub(AccountWorkflow.class,
                        workflowId)
                //Update workflow, this is a blocking operations that will
                // return once the returns once deposit method has completed
                .deposit(depositRequest.amount());

        log.info("Init deposit : " + depositRequest);
    }
}
