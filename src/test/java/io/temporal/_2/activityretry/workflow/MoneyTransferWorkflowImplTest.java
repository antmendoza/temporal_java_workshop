package io.temporal._2.activityretry.workflow;

import io.temporal.DescribeWorkflowExecution;
import io.temporal.activity.Activity;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.TransferRequest;
import io.temporal.service.AccountService;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

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

        AccountService accountService = Mockito.mock(AccountServiceImplRetry.class);

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
                new TransferRequest("fromAccount", "toAccount", "reference1", 1.23);

        // Start workflow sync
        workflow.transfer(transferRequest);

        DescribeWorkflowExecutionResponse describeResponse =
                new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule).get();

        Assert.assertEquals(
                describeResponse.getWorkflowExecutionInfo().getStatus(),
                WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

        /////////////

    }

    @Test
    public void testRetryAndSuccess() {

        AccountService accountService = Mockito.mock(AccountServiceImplRetry.class);
        doThrow(RuntimeException.class).when(accountService).deposit(any());

        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(
                new AccountService() {
                    @Override
                    public void deposit(DepositRequest depositRequest) {
                        int attends = Activity.getExecutionContext().getInfo().getAttempt();
                        if (attends <= 2) {
                            throw new NullPointerException("something is null");
                        }
                    }

                    @Override
                    public void withdraw(WithdrawRequest withdrawRequest) {
                    }
                });

        // Start server
        testWorkflowRule.getTestEnvironment().start();

        final WorkflowOptions options =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(testWorkflowRule.getTaskQueue())
                        .setWorkflowId(myWorkflowId)
                        .build();

        final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

        ///////////

        final MoneyTransferWorkflow workflow =
                workflowClient.newWorkflowStub(MoneyTransferWorkflow.class, options);

        TransferRequest transferRequest =
                new TransferRequest("fromAccount", "toAccount", "reference1", 1.23);

        // Start workflow sync
        workflow.transfer(transferRequest);

        DescribeWorkflowExecutionResponse describeResponse =
                new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule).get();

        Assert.assertEquals(
                describeResponse.getWorkflowExecutionInfo().getStatus(),
                WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

        /////////////

    }
}
