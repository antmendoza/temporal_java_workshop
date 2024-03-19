package io.temporal.exercise10.alltogether.solution;

import io.temporal.TestEnvironment;
import io.temporal.TestUtilInterceptorTracker;
import io.temporal.TestUtilWorkerInterceptor;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.exercise10.alltogether.solution.workflow.AccountWorkflow;
import io.temporal.exercise10.alltogether.solution.workflow.AccountWorkflowImpl;
import io.temporal.exercise10.alltogether.solution.workflow.child.MoneyTransferWorkflowImpl;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactoryOptions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.temporal.exercise10.alltogether.WorkerProcess.TASK_QUEUE;

public class E2ETest {

    private static TestUtilInterceptorTracker testUtilInterceptorTracker =
            new TestUtilInterceptorTracker();
    final String namespace = "default";
    final String host = "http://localhost:8000";
    final HttpClient client = HttpClient.newBuilder().build();
    @Rule
    public TestWorkflowRule testWorkflowRule = createTestRule().build();


    @After
    public void after() {
        testWorkflowRule.getTestEnvironment().shutdown();
        testUtilInterceptorTracker = new TestUtilInterceptorTracker();
    }

    @Test
    public void testE2E() throws IOException, InterruptedException {

        Worker worker = testWorkflowRule.getTestEnvironment().newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(AccountWorkflowImpl.class, MoneyTransferWorkflowImpl.class);

        // Start server
        testWorkflowRule.getTestEnvironment().start();


        final String accountId = "r-" + Math.random();
        sendHttpPUT(client, host + "/accounts",
                "{\"account-id\":\"" + accountId + "\", " +
                        "\"customer-id\":\"12346\", " +
                        "customer-name:\"Antonio\", " +
                        "customer-surname:\"MendozaPe\", " +
                        "amount=20}");


        final String workflowId = AccountWorkflow.workflowIdFromAccountId(accountId);


        final DescribeWorkflowExecutionResponse describeWorkflowExecutionResponse = getDescribeWorkflowExecutionResponse(workflowId);
        Assert.assertNotNull("Execution with id " + workflowId + " not found", describeWorkflowExecutionResponse);


        Assert.assertEquals(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING,
                describeWorkflowExecutionResponse.getWorkflowExecutionInfo().getStatus());


        sendHttpPUT(client, host + "/accounts-close",
                "{\"account-id\":\"" + accountId + "\", " +
                        "\"customer-id\":\"12346\", " +
                        "customer-name:\"Antonio\", " +
                        "customer-surname:\"MendozaPe\", " +
                        "amount=20}");

        testUtilInterceptorTracker.waitUntilWorkflowIsSignaled(1);

        Assert.assertEquals(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED,
                getDescribeWorkflowExecutionResponse(workflowId).getWorkflowExecutionInfo().getStatus());


    }

    private DescribeWorkflowExecutionResponse getDescribeWorkflowExecutionResponse(final String workflowId) {
        DescribeWorkflowExecutionResponse describe = TestEnvironment.describeWorkflowExecution(
                WorkflowExecution.newBuilder().setWorkflowId(workflowId)
                        .build(), namespace, testWorkflowRule
        );
        return describe;
    }

    private TestWorkflowRule.Builder createTestRule() {
        TestWorkflowRule.Builder builder =
                TestWorkflowRule.newBuilder()
                        .setWorkerFactoryOptions(
                                WorkerFactoryOptions.newBuilder()
                                        .setWorkerInterceptors(
                                                new TestUtilWorkerInterceptor(testUtilInterceptorTracker))
                                        .build())

                        //.setWorkflowTypes(
                        //        AccountWorkflow.class, MoneyTransferWorkflow.class)

                        .setDoNotStart(true);


        // set to true if you want to run the test with a "real" server
        boolean useExternalService = Boolean.parseBoolean(System.getenv("TEST_LOCALHOST"));

        //TODO
        useExternalService = true;


        if (useExternalService) {
            builder
                    .setUseExternalService(useExternalService)
                    .setTarget("127.0.0.1:7233") // default 127.0.0.1:7233
                    .setNamespace(namespace); // default
        }

        return builder;
    }


    private HttpResponse<Void> sendHttpPUT(HttpClient client, final String uri, final String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create(uri))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
