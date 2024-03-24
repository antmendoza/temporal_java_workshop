package io.temporal._1.firstworkflow.solution1.workflow;

import io.temporal.DescribeWorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.TransferRequest;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class MoneyTransferWorkflowImplTest {

    private final String myWorkflowId = "myWorkflow";

    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder().setDoNotStart(true).build();

    @After
    public void shutdown() {
        testWorkflowRule.getTestEnvironment().shutdown();
    }

    @Test
    public void testTransfer() {

        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);

        // Start server
        testWorkflowRule.getTestEnvironment().start();

        final WorkflowOptions options =
                WorkflowOptions.newBuilder()
                        .setWorkflowId(myWorkflowId)
                        .setTaskQueue(testWorkflowRule.getTaskQueue())
                        .build();

        final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

        ///////////

        final MoneyTransferWorkflow workflow =
                workflowClient.newWorkflowStub(MoneyTransferWorkflow.class, options);

        TransferRequest transferRequest =
                new TransferRequest("fromAccount", "toAccount",  1.23);

        // Start workflow
        String result = workflow.transfer(transferRequest);

        DescribeWorkflowExecutionResponse describeResponse =
                new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule).get();

        Assert.assertEquals(
                describeResponse.getWorkflowExecutionInfo().getStatus(),
                WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

        Assert.assertEquals(result, "done");

        /////////////

    }
}
