package io.temporal._final.springrunner;

import com.github.javafaker.Faker;
import io.temporal._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.model.TransferRequest;
import io.temporal.model.TransferState;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.temporal._final.WorkerProcess.namespace;

@Controller
public class PendingApprovalViewController {


    private static Faker fakerInstance;


    private static WorkflowClient workflowClientExecutionAPI;

    public PendingApprovalViewController() {

        if (workflowClientExecutionAPI == null) {
            final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
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

    @GetMapping("/pending-approvals")
    public String pendingApprovals(Model model) {

        final List<PendingApprovalInfoView> pendingApprovals = queryPendingApprovals();


        model.addAttribute("pendingApprovals", pendingApprovals);

        return "pending-approvals"; //view
    }



    //TODO make post
    @GetMapping("/pending-approvals/{requestId}/{state}")
    public String submitApproval(@PathVariable String requestId,
                                 @PathVariable String state,
                                 Model model) {


        //RequestId is workflowId,
        String workflowId = requestId;
        final MoneyTransferWorkflow moneyTransferWorkflow = workflowClientExecutionAPI.newWorkflowStub(MoneyTransferWorkflow.class,
                workflowId);

        final TransferState transferState = (state.equals("approve") ? TransferState.Approved : TransferState.ApprovalDenied);

        //Signal to approve / deny operation
        moneyTransferWorkflow.approveTransfer(transferState);

        return "redirect:/accounts"; //view

    }



    @RequestMapping(value="/api/pending-approvals",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity pendingApprovals() {
        return new ResponseEntity(queryPendingApprovals().size(),
                HttpStatus.OK);
    }



    private static List<PendingApprovalInfoView> queryPendingApprovals() {
        // We can query visibility API anytime as long as Temporal server is running
        //
        // Visibility API is eventually consistent, real word application should
        // store data in external db for high throughput and real time data

        // http://localhost:8233/namespaces/default/workflows?query=WorkflowType%3D%22MoneyTransferWorkflow%22+and+ExecutionStatus%3D%22Running%22+and+TransferRequestState%3D%22ApprovalRequired%22
        final String query = "WorkflowType=\"MoneyTransferWorkflow\" and ExecutionStatus=\"Running\" and " +
                "TransferRequestState=\"ApprovalRequired\"";

        final List<PendingApprovalInfoView> pendingApprovals = workflowClientVisibilityAPI()
                .listWorkflowExecutions(ListWorkflowExecutionsRequest.newBuilder()
                        .setQuery(query)
                        .setNamespace(namespace)
                        .build()).getExecutionsList().stream().map(execution -> {


                    final String workflowId = execution.getExecution().getWorkflowId();

                    final TransferRequest transferRequest =
                            workflowClientExecutionAPI.newWorkflowStub(MoneyTransferWorkflow.class, workflowId).getTransferRequest();

                    return new PendingApprovalInfoView(workflowId, transferRequest);
                }).toList();
        return pendingApprovals;
    }

}