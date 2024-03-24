package io.temporal._5.queryworklow.solution.workflow;

import io.temporal.DescribeWorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.TransferRequest;
import io.temporal.service.AccountService;
import io.temporal.service.AccountServiceImpl;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

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
    public void testTransferAuthorizationNotRequired() {

        AccountService accountService = Mockito.mock(AccountServiceImpl.class);

        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(accountService);

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
                new TransferRequest("fromAccount", "toAccount", 1.23);

        // Start workflow sync
        workflow.transfer(transferRequest);

        DescribeWorkflowExecutionResponse describeResponse =
                new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule).get();

        Assert.assertEquals(
                describeResponse.getWorkflowExecutionInfo().getStatus(),
                WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

        // QUERY STATUS
        Assert.assertEquals(workflow.queryStatus(), TRANSFER_STATUS.COMPLETED);

        /////////////

    }

    @Test
    public void testApproveTransfer() {

        AccountService accountService = Mockito.mock(AccountServiceImpl.class);

        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(accountService);

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
                new TransferRequest("fromAccount", "toAccount", 1230);

        // Start async
        WorkflowClient.start(workflow::transfer, transferRequest);

        Assert.assertEquals(
                new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule)
                        .get()
                        .getWorkflowExecutionInfo()
                        .getStatus(),
                WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING);

        // QUERY STATUS
        Assert.assertEquals(workflow.queryStatus(), TRANSFER_STATUS.WAITING_APPROVAL);

        // Approve transfer
        workflow.approveTransfer(TRANSFER_APPROVED.YES);

        // Wait workflow to complete
        workflowClient.newUntypedWorkflowStub(myWorkflowId).getResult(Void.class);

        Assert.assertEquals(
                new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule)
                        .get()
                        .getWorkflowExecutionInfo()
                        .getStatus(),
                WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

        // QUERY STATUS
        Assert.assertEquals(workflow.queryStatus(), TRANSFER_STATUS.COMPLETED);

        /////////////

    }

    @Test
    public void testDenyTransfer() {

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
                new TransferRequest("fromAccount", "toAccount", 1230);

        // Start async
        WorkflowClient.start(workflow::transfer, transferRequest);

        Assert.assertEquals(
                new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule)
                        .get()
                        .getWorkflowExecutionInfo()
                        .getStatus(),
                WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING);

        // QUERY STATUS
        Assert.assertEquals(workflow.queryStatus(), TRANSFER_STATUS.WAITING_APPROVAL);

        // Approve transfer
        workflow.approveTransfer(TRANSFER_APPROVED.NO);

        // Wait workflow to complete
        workflowClient.newUntypedWorkflowStub(myWorkflowId).getResult(Void.class);

        Assert.assertEquals(
                new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule)
                        .get()
                        .getWorkflowExecutionInfo()
                        .getStatus(),
                WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

        // QUERY STATUS
        Assert.assertEquals(workflow.queryStatus(), TRANSFER_STATUS.DENIED);
        /////////////

    }
}
