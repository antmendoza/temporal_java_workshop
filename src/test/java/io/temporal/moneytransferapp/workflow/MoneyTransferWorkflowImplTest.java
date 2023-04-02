package io.temporal.moneytransferapp.workflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.moneytransferapp.activity.AccountService;
import io.temporal.moneytransferapp.activity.AccountServiceImpl;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

public class MoneyTransferWorkflowImplTest {


    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setDoNotStart(true)
                    .build();


    @Test
    public void testTransfer() {


        AccountService accountService = Mockito.mock(AccountServiceImpl.class);


        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(accountService);


        testWorkflowRule.getTestEnvironment().start();

        final WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(testWorkflowRule.getTaskQueue())
                .build();

        final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

        ///////////


        final MoneyTransferWorkflow workflow = workflowClient
                .newWorkflowStub(MoneyTransferWorkflow.class, options);

        final String result = workflow.transfer("account1", "account2", "reference1", 1.23);

        Assert.assertEquals(result, "some-transference-id");

        /////////////


    }

}