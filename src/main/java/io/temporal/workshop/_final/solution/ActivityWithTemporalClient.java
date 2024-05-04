package io.temporal.workshop._final.solution;

import io.temporal.workshop._final.AccountWorkflow;
import io.temporal.workshop.activity.AccountService;
import io.temporal.workshop.service.DepositRequest;
import io.temporal.workshop.service.WithdrawRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class ActivityWithTemporalClient implements AccountService {

    private final Logger log = Workflow.getLogger(ActivityWithTemporalClient.class.getSimpleName());
    private final WorkflowClient client;

    public ActivityWithTemporalClient(WorkflowClient client) {
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
                .deposit(depositRequest.amount());

        log.info("Init deposit : " + depositRequest);
    }
}
