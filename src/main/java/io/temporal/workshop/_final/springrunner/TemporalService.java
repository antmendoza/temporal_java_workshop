package io.temporal.workshop._final.springrunner;

import io.temporal.workshop.Constants;
import io.temporal.workshop._final.solution.workflow.AccountWorkflow;
import io.temporal.workshop._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.workshop.model.AccountSummaryResponse;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.temporal.workshop.Constants.namespace;


@Component
public class TemporalService {

    private static WorkflowClient workflowClientExecutionAPI;
    private static WorkflowServiceGrpc.WorkflowServiceBlockingStub workflowClientQueryAPI;


    public TemporalService() {
        if (workflowClientExecutionAPI == null) {

            //We could have used https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha
            final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                    .newBuilder()
                    .setTarget(Constants.targetGRPC)
                    .build());
            workflowClientExecutionAPI = WorkflowClient.newInstance(service);


            workflowClientQueryAPI = workflowClientExecutionAPI.getWorkflowServiceStubs()
                    .blockingStub();
        }
    }


    public static AccountInfoView getAccountInfoView(final String workflowId) {
        //This query is performed by our Worker entity (no internal state is stored in the server)
        final AccountSummaryResponse accountSummary =
                workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class, workflowId).getAccountSummary();


        final WorkflowExecutionStatus workflowExecutionStatus = getWorkflowExecutionStatus(workflowId, workflowClientExecutionAPI);

        final String status = WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING
                .equals(workflowExecutionStatus) ? "Open" : "Closed";

        return new AccountInfoView(workflowId, accountSummary, status);
    }


    private static WorkflowExecutionStatus getWorkflowExecutionStatus(final String workflowId, final WorkflowClient workflowClientExecutionAPI) {
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


    public List<AccountInfoView> getAccounts() {

        // Visibility API is eventually consistent.
        // Real word applications that requires high throughput and real time data should
        // store/query data in external DBs

        // http://localhost:8080/namespaces/default/workflows?query=WorkflowType%3D%22AccountWorkflow%22
        final String query = "WorkflowType=\"AccountWorkflow\"";
        return workflowClientQueryAPI
                .listWorkflowExecutions(ListWorkflowExecutionsRequest.newBuilder()
                        .setQuery(query)
                        .setNamespace(namespace)
                        .build()).getExecutionsList().stream().map((execution) ->
                {
                    final String workflowId = execution.getExecution().getWorkflowId();

                    return getAccountInfoView(workflowId);

                }).toList();

    }

    public List<AccountInfoView> getOpenAccounts() {


        // http://localhost:8080/namespaces/default/workflows?query=WorkflowType%3D%22AccountWorkflow%22+AND+ExecutionStatus%3D%22Running%22
        final String query = "WorkflowType=\"AccountWorkflow\" AND ExecutionStatus=\"Running\"";
        return workflowClientQueryAPI
                .listWorkflowExecutions(ListWorkflowExecutionsRequest.newBuilder()
                        .setQuery(query)
                        .setNamespace(namespace)
                        .build()).getExecutionsList().stream().map((execution) ->
                {
                    final String workflowId = execution.getExecution().getWorkflowId();

                    return getAccountInfoView(workflowId);
                }).toList();

    }


    private List<PendingRequestInfoView> queryRequest(final String query) {

        // Visibility API is eventually consistent.
        // Real word applications that requires high throughput and real time data should
        // store/query data in external DBs
        final ListWorkflowExecutionsRequest listWorkflowExecutionsRequest = ListWorkflowExecutionsRequest.newBuilder()
                .setQuery(query)
                .setNamespace(namespace)
                .build();

        final List<PendingRequestInfoView> pendingApprovals = workflowClientQueryAPI
                .listWorkflowExecutions(listWorkflowExecutionsRequest).getExecutionsList().stream().map(execution -> {

                    //For each workflow running and waiting for approval
                    final String workflowId = execution.getExecution().getWorkflowId();

                    // Query the workflow through the queryMethod getTransferRequest to retrieve internal state (stored as a workflow variable)
                    final TransferRequest transferRequest =
                            workflowClientExecutionAPI.newWorkflowStub(MoneyTransferWorkflow.class, workflowId).getTransferRequest();

                    return new PendingRequestInfoView(workflowId, transferRequest);
                }).toList();
        return pendingApprovals;
    }


    public List<PendingRequestInfoView> getPendingRequests() {


        // http://localhost:8080/namespaces/default/workflows?query=WorkflowType%3D%22MoneyTransferWorkflow%22+and+ExecutionStatus%3D%22Running%22+and+TransferRequestStatus%3D%22ApprovalRequired%22
        final String query = "WorkflowType=\"MoneyTransferWorkflow\" and ExecutionStatus=\"Running\" and " +
                "TransferRequestStatus=\"ApprovalRequired\"";


        return queryRequest(query);
    }

    public List<PendingRequestInfoView> getRequests() {
        // http://localhost:8080/namespaces/default/workflows?query=WorkflowType%3D%22MoneyTransferWorkflow%22
        final String query = "WorkflowType=\"MoneyTransferWorkflow\"";
        return queryRequest(query);

    }


}
