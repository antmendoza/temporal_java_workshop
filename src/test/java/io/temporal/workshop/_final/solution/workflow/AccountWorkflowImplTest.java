package io.temporal.workshop._final.solution.workflow;

import io.temporal.workshop.TestUtilInterceptorTracker;
import io.temporal.workshop.TestUtilWorkerInterceptor;
import io.temporal.workshop._final.solution.AccountServiceWithTemporalClient;
import io.temporal.workshop._final.AccountWorkflow;
import io.temporal.workshop._final.solution.AccountWorkflowImpl;
import io.temporal.workshop.activity.NotificationService;
import io.temporal.workshop.activity.NotificationServiceImpl;
import io.temporal.workshop._final.AccountCleanUpWorkflow;
import io.temporal.workshop._final.solution.AccountCleanUpWorkflowImpl;
import io.temporal.workshop._final.solution.MoneyTransferWorkflowImpl;
import io.temporal.api.workflowservice.v1.ListClosedWorkflowExecutionsRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.workshop.model.Account;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.activity.AccountService;
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
    private static final String namespace = "default";
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
                   // .setUseExternalService(true)
                    .build();


    @After
    public void after() {
        testWorkflowRule.getTestEnvironment().shutdown();
        testUtilInterceptorTracker = new TestUtilInterceptorTracker();
    }

    @Test
    public void testE2E() {

        final AccountService accountService = new AccountServiceWithTemporalClient(testWorkflowRule.getWorkflowClient());
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

        //Source workflow
        final String sourceAccountId = "r-" + Math.random();
        final String sourceWorkflowId = AccountWorkflow.workflowIdFromAccountId(sourceAccountId);
        final AccountWorkflow sourceWorkflow = workflowClient.newWorkflowStub(AccountWorkflow.class, WorkflowOptions
                .newBuilder()
                .setWorkflowId(sourceWorkflowId)
                .setTaskQueue(testWorkflowRule.getTaskQueue())
                .build());
        //Start workflow async, so we don't block the test here until the workflow completes
        WorkflowClient.start(sourceWorkflow::open, new Account(sourceAccountId, "customerName", 50));




        //Target workflow
        final String targetAccountId = "r-" + Math.random();
        final String targetWorkflowId = AccountWorkflow.workflowIdFromAccountId(targetAccountId);
        final AccountWorkflow targetWorkflow = workflowClient.newWorkflowStub(AccountWorkflow.class, WorkflowOptions
                .newBuilder()
                .setWorkflowId(targetWorkflowId)
                .setTaskQueue(testWorkflowRule.getTaskQueue())
                .build());
        //Start workflow async, so we don't block the test here until the workflow completes
        WorkflowClient.start(targetWorkflow::open, new Account(targetAccountId, "customerName", 50));


        // Start 3 request to transfer money
        sourceWorkflow.requestTransfer(new TransferRequest(sourceAccountId, targetAccountId,  10));
        sourceWorkflow.requestTransfer(new TransferRequest(sourceAccountId, targetAccountId,  10));
        sourceWorkflow.requestTransfer(new TransferRequest(sourceAccountId, targetAccountId,  10));


        // It will start 3 child workflows, lets wait until those workflows finish
        TestUtilInterceptorTracker.waitUntilTrue(new TestUtilInterceptorTracker.Awaitable(
                () -> testWorkflowRule.getWorkflowClient().getWorkflowServiceStubs()
                        .blockingStub().listClosedWorkflowExecutions(ListClosedWorkflowExecutionsRequest.newBuilder()
                                .setNamespace(namespace)
                                .build()).getExecutionsCount() == 3)
        );

        //close account
        var closeAccountResponse = sourceWorkflow.closeAccount();

        //Verify amount in the account after closing it
        final double accountAmount = closeAccountResponse.account().balance();
        Assert.assertEquals(20, accountAmount, 0.2);

        testUtilInterceptorTracker.waitUntilWorkflowIsClosed(sourceWorkflowId);

        // We have introduced an artificial sleep to the child workflow, just to show the child
        // workflow can continue running after the parent workflow is closed,
        // Let's "travel in time" forward 5 second
        testWorkflowRule.getTestEnvironment().sleep(Duration.ofSeconds(5));
        testUtilInterceptorTracker.waitUntilWorkflowIsClosed(AccountCleanUpWorkflow.workflowIdFromAccountId(sourceAccountId));

    }


}