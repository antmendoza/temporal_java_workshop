package io.temporal._final.solution.workflow;

import io.temporal.TestEnvironment;
import io.temporal.TestUtilInterceptorTracker;
import io.temporal.TestUtilWorkerInterceptor;
import io.temporal.activity.AccountService;
import io.temporal.activity.AccountServiceImpl;
import io.temporal.activity.NotificationService;
import io.temporal.activity.NotificationServiceImpl;
import io.temporal._final.solution.workflow.child.AccountCleanUpWorkflow;
import io.temporal._final.solution.workflow.child.AccountCleanUpWorkflowImpl;
import io.temporal._final.solution.workflow.child.MoneyTransferWorkflowImpl;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.api.workflowservice.v1.ListClosedWorkflowExecutionsRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.Account;
import io.temporal.model.Transfer;
import io.temporal.model.TransferRequest;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactoryOptions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;

public class AccountWorkflowImplTest {

    // Namespace is dynamically set
    private static final String namespace = "test-namespace";
    private static TestUtilInterceptorTracker testUtilInterceptorTracker =
            new TestUtilInterceptorTracker();
    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setNamespace(namespace)
                    .setWorkerFactoryOptions(
                            WorkerFactoryOptions.newBuilder()
                                    .setWorkerInterceptors(
                                            new TestUtilWorkerInterceptor(testUtilInterceptorTracker))
                                    .build())
                    .setDoNotStart(true)
                    .build();


    @After
    public void after() {
        testWorkflowRule.getTestEnvironment().shutdown();
        testUtilInterceptorTracker = new TestUtilInterceptorTracker();
    }

    @Test
    public void testE2E() {

        final AccountService accountService = Mockito.mock(AccountServiceImpl.class);
        final NotificationService notificationService = Mockito.mock(NotificationServiceImpl.class);

        //Setup worker, register workflows and activities
        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(
                AccountWorkflowImpl.class,
                MoneyTransferWorkflowImpl.class,
                AccountCleanUpWorkflowImpl.class);
        worker.registerActivitiesImplementations(accountService);
        worker.registerActivitiesImplementations(notificationService);

        // Start test server
        testWorkflowRule.getTestEnvironment().start();

        //Workflow client to interact with Temporal server
        final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

        //setup ids
        final String accountId = "r-" + Math.random();
        final String workflowId = AccountWorkflow.workflowIdFromAccountId(accountId);

        final AccountWorkflow workflow = workflowClient.newWorkflowStub(AccountWorkflow.class, WorkflowOptions
                .newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(testWorkflowRule.getTaskQueue())
                .build());

        //Start workflow async, so we don't block the test here until the workflow completes
        WorkflowClient.start(workflow::open, new Account(accountId, "customerId", 50));


        // Start 3 request to transfer money
        workflow.requestTransfer(new TransferRequest(accountId, "" + Math.random(),  10));
        workflow.requestTransfer(new TransferRequest(accountId, "" + Math.random(),  10));
        workflow.requestTransfer(new TransferRequest(accountId, "" + Math.random(),  10));


        // It will start 3 child workflows, lets wait until those workflows finish
        TestUtilInterceptorTracker.waitUntilTrue(new TestUtilInterceptorTracker.Awaitable(
                () -> testWorkflowRule.getWorkflowClient().getWorkflowServiceStubs()
                        .blockingStub().listClosedWorkflowExecutions(ListClosedWorkflowExecutionsRequest.newBuilder()
                                .setNamespace(namespace)
                                .build()).getExecutionsCount() == 3)
        );

        //close account
        var closeAccountResponse = workflow.closeAccount();

        //Verify amount in the account after closing it
        final double accountAmount = closeAccountResponse.account().amount();
        Assert.assertEquals(20, accountAmount, 0.2);

        testUtilInterceptorTracker.waitUntilWorkflowIsClosed(workflowId);

        // We have introduced an artificial sleep to the child workflow just to show the child
        // workflow can continue running after the parent workflow is closed,
        // Let's "travel in time" forward 5 second
        testWorkflowRule.getTestEnvironment().sleep(Duration.ofSeconds(5));
        testUtilInterceptorTracker.waitUntilWorkflowIsClosed(AccountCleanUpWorkflow.workflowIdFromAccountId(accountId));

    }

    private DescribeWorkflowExecutionResponse getDescribeWorkflowExecutionResponse(final String workflowId, String namespace) {
        DescribeWorkflowExecutionResponse describe = TestEnvironment.describeWorkflowExecution(
                WorkflowExecution.newBuilder().setWorkflowId(workflowId)
                        .build(), namespace, testWorkflowRule
        );
        return describe;
    }


}