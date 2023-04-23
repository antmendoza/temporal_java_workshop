package io.temporal.demo10.childworkflow.solution;

import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class ListCompletedWorkflows {

  public static void main(String[] args) {

    final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    while (true) {

      // You can paste this query in the UI, advance search
      final String value =
          "ExecutionStatus=\"Completed\" and WorkflowType=\"MoneyTransferChildWorkflow\"";
      int numClosedExecution =
          service
              .blockingStub()
              .listWorkflowExecutions(
                  // https://docs.temporal.io/visibility#search-attribute
                  ListWorkflowExecutionsRequest.newBuilder()
                      .setQuery(value)
                      .setNamespace("default")
                      .build())
              .getExecutionsCount();

      System.out.println(">>>>> numClosedExecution " + numClosedExecution);

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
