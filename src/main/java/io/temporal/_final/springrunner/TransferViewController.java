package io.temporal._final.springrunner;

import com.github.javafaker.Faker;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.failure.TemporalException;
import io.temporal.model.RequestTransferResponse;
import io.temporal.model.TransferRequest;
import io.temporal.model.TransferStatus;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static io.temporal.Constants.namespace;

@Controller
public class TransferViewController {

    private static Faker fakerInstance;

    private static WorkflowClient workflowClientExecutionAPI;

    public TransferViewController() {

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


    @GetMapping("/transfer-request/{fromAccountId}")
    public String newTransferView(@PathVariable String fromAccountId, Model model) {

        //Dummy values
        final String toAccountId = "" + fakerInstance.random().nextInt(100_000, 1_000_000);
        final int amount = fakerInstance.random().nextInt(10, 100);

        final TransferRequest transferRequest = new TransferRequest(fromAccountId, toAccountId, amount);

        model.addAttribute("transferRequest", transferRequest);

        return "transfer-request"; //navigate to view
    }


    @PostMapping("/transfers")
    public String requestTransfer(@ModelAttribute("transferRequest") TransferRequest transferRequest,
                                  Model model,
                                  RedirectAttributes redirectAttrs) {


        try {

            //We need the workflow id to signal the workflow, to request the transference
            final String workflowId = AccountWorkflow.workflowIdFromAccountId(transferRequest.fromAccountId());

            final AccountWorkflow accountWorkflow = workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class,
                    workflowId);

            //Signals are async request to server-> workflow execution. This line will unblock when the server ack the
            // reception of the request
            RequestTransferResponse response = accountWorkflow.requestTransfer(transferRequest);

            redirectAttrs.addFlashAttribute("msg", "Request created; operation id " +
                    response.getOperationId());


        } catch (TemporalException e) {
            redirectAttrs.addFlashAttribute("msg", e.getCause());
        }

        return "redirect:/accounts"; //navigate to view

    }


    @GetMapping("/transfers")
    public String requests(Model model) {

        final List<PendingRequestInfoView> transfers = queryPendingApprovals(getAllRequests());

        model.addAttribute("transfers", transfers);

        return "transfers"; //navigate to view
    }


    @GetMapping("/pending-transfers")
    public String pendingRequests(Model model) {

        final List<PendingRequestInfoView> pendingRequests = queryPendingApprovals(getQueryPendingRequests());

        model.addAttribute("pendingRequests", pendingRequests);

        return "pending-transfers"; //navigate to view
    }

    //TODO make post
    @GetMapping("/pending-transfers/{requestId}/{status}")
    public String changeTransferStatus(@PathVariable String requestId,
                                       @PathVariable String status,
                                       Model model,
                                       RedirectAttributes redirectAttrs) {


        try {


            //We have set in the view workflowId as requestId
            String workflowId = requestId;
            final MoneyTransferWorkflow moneyTransferWorkflow = workflowClientExecutionAPI.newWorkflowStub(MoneyTransferWorkflow.class,
                    workflowId);

            final TransferStatus transferStatus = (status.equals("approve") ? TransferStatus.Approved : TransferStatus.Denied);


            //Signal to approve / deny operation.
            //Signals are async request to server-> workflow execution. This line will unblock when the server ack the
            // reception of the request
            moneyTransferWorkflow.approveTransfer(transferStatus);

            return "redirect:/accounts"; //navigate to view

        } catch (TemporalException e) {
            redirectAttrs.addFlashAttribute("msg", e.getCause());
            return "redirect:/pending-transfers"; //navigate to view
        }

    }

    @RequestMapping(value = "/api/pending-transfers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity pendingApprovals() {
        return new ResponseEntity(queryPendingApprovals(getQueryPendingRequests()).size(),
                HttpStatus.OK);
    }

    private WorkflowServiceGrpc.WorkflowServiceBlockingStub workflowClientVisibilityAPI() {
        return workflowClientExecutionAPI.getWorkflowServiceStubs()
                .blockingStub();
    }

    private List<PendingRequestInfoView> queryPendingApprovals(final String query) {

        // Visibility API is eventually consistent.
        // Real word applications that requires high throughput and real time data should
        // store/query data in external DBs
        final ListWorkflowExecutionsRequest listWorkflowExecutionsRequest = ListWorkflowExecutionsRequest.newBuilder()
                .setQuery(query)
                .setNamespace(namespace)
                .build();

        final List<PendingRequestInfoView> pendingApprovals = workflowClientVisibilityAPI()
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




    private static String getQueryPendingRequests() {
        // http://localhost:8233/namespaces/default/workflows?query=WorkflowType%3D%22MoneyTransferWorkflow%22+and+ExecutionStatus%3D%22Running%22+and+TransferRequestStatus%3D%22ApprovalRequired%22
        final String query = "WorkflowType=\"MoneyTransferWorkflow\" and ExecutionStatus=\"Running\" and " +
                "TransferRequestStatus=\"ApprovalRequired\"";
        return query;
    }

    private static String getAllRequests() {
        // http://localhost:8233/namespaces/default/workflows?query=WorkflowType%3D%22MoneyTransferWorkflow%22
        final String query = "WorkflowType=\"MoneyTransferWorkflow\"";
        return query;
    }


}