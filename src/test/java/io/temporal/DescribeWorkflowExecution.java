package io.temporal;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.testing.TestWorkflowRule;

public class DescribeWorkflowExecution {

  private final String myWorkflowId;
  private final TestWorkflowRule testWorkflowRule;

  public DescribeWorkflowExecution(String myWorkflowId, TestWorkflowRule testWorkflowRule) {
    this.myWorkflowId = myWorkflowId;
    this.testWorkflowRule = testWorkflowRule;
  }

  private static DescribeWorkflowExecutionResponse describeWorkflowExecutionResponse(
      WorkflowExecution.Builder myWorkflow, TestWorkflowRule testWorkflowRule) {
    String namespace = testWorkflowRule.getTestEnvironment().getNamespace();
    DescribeWorkflowExecutionResponse describeResponse =
        testWorkflowRule
            .getTestEnvironment()
            .getWorkflowClient()
            .getWorkflowServiceStubs()
            .blockingStub()
            .describeWorkflowExecution(
                DescribeWorkflowExecutionRequest.newBuilder()
                    .setNamespace(namespace)
                    .setExecution(myWorkflow.build())
                    .build());
    return describeResponse;
  }

  public DescribeWorkflowExecutionResponse get() {

    return describeWorkflowExecutionResponse(
        WorkflowExecution.newBuilder().setWorkflowId(myWorkflowId), testWorkflowRule);
  }
}
