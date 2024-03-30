package io.temporal._final.springrunner;

import com.github.javafaker.Faker;
import io.temporal._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.model.TransferRequest;
import io.temporal.model.TransferState;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.temporal.Constants.namespace;


@Controller
public class PendingApprovalViewController {


    private static Faker fakerInstance;


    private static WorkflowClient workflowClientExecutionAPI;

    public PendingApprovalViewController() {

        if (workflowClientExecutionAPI == null) {
            //We could have used https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha
            final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                    .newBuilder()
                    .setTarget(io.temporal.Constants.targetGRPC)
                    .build());
            workflowClientExecutionAPI = WorkflowClient.newInstance(service);
        }

        if (fakerInstance == null) {
            fakerInstance = Faker.instance();
        }

    }

    private static WorkflowServiceGrpc.WorkflowServiceBlockingStub workflowClientVisibilityAPI() {
        return workflowClientExecutionAPI.getWorkflowServiceStubs()
                .blockingStub();
    }

    private static List<PendingApprovalInfoView> queryPendingApprovals() {

        // Visibility API is eventually consistent.
        // Real word applications that requires high throughput and real time data should
        // store/query data in external DBs

        // http://localhost:8233/namespaces/default/workflows?query=WorkflowType%3D%22MoneyTransferWorkflow%22+and+ExecutionStatus%3D%22Running%22+and+TransferRequestState%3D%22ApprovalRequired%22
        final String query = "WorkflowType=\"MoneyTransferWorkflow\" and ExecutionStatus=\"Running\" and " +
                "TransferRequestState=\"ApprovalRequired\"";

        final ListWorkflowExecutionsRequest listWorkflowExecutionsRequest = ListWorkflowExecutionsRequest.newBuilder()
                .setQuery(query)
                .setNamespace(namespace)
                .build();

        final List<PendingApprovalInfoView> pendingApprovals = workflowClientVisibilityAPI()
                .listWorkflowExecutions(listWorkflowExecutionsRequest).getExecutionsList().stream().map(execution -> {

                    //For each workflow running and waiting for approval
                    final String workflowId = execution.getExecution().getWorkflowId();

                    // Query the workflow through the queryMethod getTransferRequest to retrieve internal state (stored as a workflow variable)
                    final TransferRequest transferRequest =
                            workflowClientExecutionAPI.newWorkflowStub(MoneyTransferWorkflow.class, workflowId).getTransferRequest();

                    return new PendingApprovalInfoView(workflowId, transferRequest);
                }).toList();
        return pendingApprovals;
    }

    @GetMapping("/pending-approvals")
    public String pendingApprovals(Model model) {

        final List<PendingApprovalInfoView> pendingApprovals = queryPendingApprovals();


        model.addAttribute("pendingApprovals", pendingApprovals);

        return "pending-approvals"; //navigate to view
    }

    //TODO make post
    @GetMapping("/pending-approvals/{requestId}/{state}")
    public String submitApproval(@PathVariable String requestId,
                                 @PathVariable String state,
                                 Model model) {


        //We have set in the view workflowId as requestId
        String workflowId = requestId;
        final MoneyTransferWorkflow moneyTransferWorkflow = workflowClientExecutionAPI.newWorkflowStub(MoneyTransferWorkflow.class,
                workflowId);

        final TransferState transferState = (state.equals("approve") ? TransferState.Approved : TransferState.ApprovalDenied);


        //Signal to approve / deny operation.
        //Signals are async request to server-> workflow execution. This line will unblock when the server ack the
        // reception of the request
        moneyTransferWorkflow.approveTransfer(transferState);

        return "redirect:/accounts"; //navigate to view

    }

    @RequestMapping(value = "/api/pending-approvals",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity pendingApprovals() {
        return new ResponseEntity(queryPendingApprovals().size(),
                HttpStatus.OK);
    }

}