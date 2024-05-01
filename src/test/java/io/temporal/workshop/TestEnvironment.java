package io.temporal.workshop;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.testing.TestWorkflowRule;

public class TestEnvironment {
    public static DescribeWorkflowExecutionResponse describeWorkflowExecution(
            WorkflowExecution execution, String namespace, TestWorkflowRule testWorkflowRule) {

        DescribeWorkflowExecutionRequest.Builder builder =
                DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(namespace)
                        // .setExecution(
                        //   WorkflowExecution.newBuilder()
                        //       .setWorkflowId(execution.getWorkflowId())
                        //       .setRunId(execution.getRunId())
                        //       .build())
                        .setExecution(execution);
        DescribeWorkflowExecutionResponse result =
                testWorkflowRule
                        .getTestEnvironment()
                        .getWorkflowServiceStubs()
                        .blockingStub()
                        .describeWorkflowExecution(builder.build());
        return result;
    }
}
