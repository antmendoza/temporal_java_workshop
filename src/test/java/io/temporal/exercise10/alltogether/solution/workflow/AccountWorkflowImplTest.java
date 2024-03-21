package io.temporal.exercise10.alltogether.solution.workflow;

import io.temporal.TestEnvironment;
import io.temporal.TestUtilInterceptorTracker;
import io.temporal.TestUtilWorkerInterceptor;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.exercise10.alltogether.solution.workflow.child.AccountCleanUpWorkflow;
import io.temporal.exercise10.alltogether.solution.workflow.child.AccountCleanUpWorkflowImpl;
import io.temporal.exercise10.alltogether.solution.workflow.child.MoneyTransferWorkflowImpl;
import io.temporal.exercise10.alltogether.solution.workflow.child.activity.AccountService;
import io.temporal.exercise10.alltogether.solution.workflow.child.activity.AccountServiceImpl;
import io.temporal.exercise10.alltogether.solution.workflow.child.activity.NotificationService;
import io.temporal.exercise10.alltogether.solution.workflow.child.activity.NotificationServiceImpl;
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

    private static TestUtilInterceptorTracker testUtilInterceptorTracker =
            new TestUtilInterceptorTracker();
    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
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

        Worker worker = testWorkflowRule.getWorker();
        worker.registerWorkflowImplementationTypes(
                AccountWorkflowImpl.class,
                MoneyTransferWorkflowImpl.class,
                AccountCleanUpWorkflowImpl.class);
        worker.registerActivitiesImplementations(accountService);
        worker.registerActivitiesImplementations(notificationService);

        // Start server
        testWorkflowRule.getTestEnvironment().start();

        final String accountId = "r-" + Math.random();
        final String workflowId = AccountWorkflow.workflowIdFromAccountId(accountId);

        final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

        final AccountWorkflow workflow = workflowClient.newWorkflowStub(AccountWorkflow.class, WorkflowOptions
                .newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(testWorkflowRule.getTaskQueue())
                .build());

        //Start async
        WorkflowClient.start(workflow::open, new Account(accountId, "customerId", 20));

        final String namespace = testWorkflowRule.
                getTestEnvironment()
                .getNamespace();
        final DescribeWorkflowExecutionResponse describeWorkflowExecutionResponse = getDescribeWorkflowExecutionResponse(workflowId,
                namespace);

        Assert.assertEquals(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING,
                describeWorkflowExecutionResponse.getWorkflowExecutionInfo().getStatus());


        //Send signal
       // workflow.requestTransfer(new Transfer(""+Math.random(), "customerId", 10));
       // workflow.requestTransfer(new Transfer(""+Math.random(), "customerId", 10));
       // workflow.requestTransfer(new Transfer(""+Math.random(), "customerId", 10));

        //close account
        workflow.closeAccount();

        testWorkflowRule.getTestEnvironment().sleep(Duration.ofSeconds(5));

        testUtilInterceptorTracker.waitUntilWorkflowIsClosed(workflowId);

        Assert.assertEquals(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED,
                getDescribeWorkflowExecutionResponse(workflowId, namespace).getWorkflowExecutionInfo().getStatus());


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