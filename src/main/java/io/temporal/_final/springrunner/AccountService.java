package io.temporal._final.springrunner;

import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.model.AccountSummaryResponse;

import java.util.List;

import static io.temporal.Constants.namespace;

public class AccountService {


    public static AccountInfoView getAccountInfoView(final String workflowId, final WorkflowClient workflowClientExecutionAPI) {
        //This query is performed by our Worker entity (no internal state is stored in the server)
        final AccountSummaryResponse accountSummary =
                workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class, workflowId).getAccountSummary();


        final WorkflowExecutionStatus workflowExecutionStatus = getWorkflowExecutionStatus(workflowId, workflowClientExecutionAPI);

        final String status = WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING
                .equals(workflowExecutionStatus) ? "Open" : "Closed";

        return new AccountInfoView(workflowId, accountSummary, status);
    }


    public static WorkflowExecutionStatus getWorkflowExecutionStatus(final String workflowId, final WorkflowClient workflowClientExecutionAPI) {
        final DescribeWorkflowExecutionResponse describeNamespaceResponse = workflowClientExecutionAPI.getWorkflowServiceStubs().blockingStub()
                .describeWorkflowExecution(DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(namespace)
                        .setExecution(WorkflowExecution.newBuilder()
                                .setWorkflowId(workflowId)
                                .build())
                        .build());

        final WorkflowExecutionStatus workflowExecutionStatus = describeNamespaceResponse.getWorkflowExecutionInfo().getStatus();
        return workflowExecutionStatus;
    }


    public static List<AccountInfoView> getAccounts(
            final WorkflowServiceGrpc.WorkflowServiceBlockingStub workflowClientVisibilityAPI,
            final WorkflowClient workflowClientExecutionAPI
    ) {
        // Visibility API is eventually consistent.
        // Real word applications that requires high throughput and real time data should
        // store/query data in external DBs

        // http://localhost:8233/namespaces/default/workflows?query=WorkflowType%3D%22AccountWorkflow%22
        final String query = "WorkflowType=\"AccountWorkflow\"";
        return workflowClientVisibilityAPI
                .listWorkflowExecutions(ListWorkflowExecutionsRequest.newBuilder()
                        .setQuery(query)
                        .setNamespace(namespace)
                        .build()).getExecutionsList().stream().map((execution) ->
                {
                    final String workflowId = execution.getExecution().getWorkflowId();

                    return getAccountInfoView(workflowId, workflowClientExecutionAPI);

                }).toList();

    }

    public static List<AccountInfoView> getOpenAccounts(
            final WorkflowServiceGrpc.WorkflowServiceBlockingStub workflowClientVisibilityAPI,
            final WorkflowClient workflowClientExecutionAPI
    ) {


        // http://localhost:8233/namespaces/default/workflows?query=WorkflowType%3D%22AccountWorkflow%22+AND+ExecutionStatus%3D%22Running%22
        final String query = "WorkflowType=\"AccountWorkflow\" AND ExecutionStatus=\"Running\"";
        return workflowClientVisibilityAPI
                .listWorkflowExecutions(ListWorkflowExecutionsRequest.newBuilder()
                        .setQuery(query)
                        .setNamespace(namespace)
                        .build()).getExecutionsList().stream().map((execution) ->
                {
                    final String workflowId = execution.getExecution().getWorkflowId();

                    return getAccountInfoView(workflowId, workflowClientExecutionAPI);
                }).toList();

    }
}
