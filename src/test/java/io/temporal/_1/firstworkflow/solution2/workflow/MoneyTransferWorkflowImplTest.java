package io.temporal._1.firstworkflow.solution2.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.temporal.DescribeWorkflowExecution;
import io.temporal._1.firstworkflow.solution2.workflow.activity.AccountService;
import io.temporal._1.firstworkflow.solution2.workflow.activity.AccountServiceImpl;
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
  public void testTransfer() {

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
        new TransferRequest("fromAccount", "toAccount", "reference1", 1.23);

    // Start workflow
    String result = workflow.transfer(transferRequest);

    DescribeWorkflowExecutionResponse describeResponse =
        new DescribeWorkflowExecution(myWorkflowId, testWorkflowRule).get();

    Assert.assertEquals(
        describeResponse.getWorkflowExecutionInfo().getStatus(),
        WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);

    Assert.assertEquals(result, "done");

    verify(accountService, times(1)).deposit(any());
    verify(accountService, times(1)).withdraw(any());

    /////////////

  }
}
