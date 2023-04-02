package io.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.moneytransferapp.workflow.MoneyTransferWorkflow;
import io.temporal.moneytransferapp.workflow.MoneyTransferWorkflowImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class MoneyTransferAppTest {


    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setWorkflowTypes(MoneyTransferWorkflowImpl.class)
                    .setDoNotStart(true)
                    .build();


    @Test
    public void testTransfer() {

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
