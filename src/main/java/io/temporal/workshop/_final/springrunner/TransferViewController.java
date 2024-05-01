package io.temporal.workshop._final.springrunner;

import com.github.javafaker.Faker;
import io.temporal.workshop.Constants;
import io.temporal.workshop._final.solution.workflow.AccountWorkflow;
import io.temporal.workshop._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.workshop.model.RequestTransferResponse;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workshop.model.TransferStatus;
import io.temporal.client.WorkflowClient;
import io.temporal.failure.TemporalException;
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
import java.util.Objects;

@Controller
public class TransferViewController {


    private static Faker fakerInstance;
    private final TemporalService temporalService;

    private static WorkflowClient workflowClientExecutionAPI;

    public TransferViewController(final TemporalService temporalService) {
        this.temporalService = temporalService;

        if (fakerInstance == null) {
            fakerInstance = Faker.instance();
        }

        if (workflowClientExecutionAPI == null) {
            //We could have used https://github.com/temporalio/sdk-java/tree/master/temporal-spring-boot-autoconfigure-alpha
            final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                    .newBuilder()
                    .setTarget(Constants.targetGRPC)
                    .build());
            workflowClientExecutionAPI = WorkflowClient.newInstance(service);
        }

    }


    @GetMapping("/transfer-request/{fromAccountId}")
    public String newTransferView(@PathVariable String fromAccountId,
                                  Model model,
                                  RedirectAttributes redirectAttrs) {

        //Dummy values
        final int amount = fakerInstance.random().nextInt(10, 100);

        final List<String> accounts =
                this.temporalService.getOpenAccounts()
                        .stream().map(accountInfo -> accountInfo.accountSummary().account().accountId())
                        .filter(accountId -> {
                            return !Objects.equals(accountId, fromAccountId);
                        }).toList();

        model.addAttribute("accounts", accounts);


        if (accounts.isEmpty()) {
            redirectAttrs.addFlashAttribute("msg", "Please, create one more account!");
            return "redirect:/accounts"; //navigate to view

        }

        final String toAccountId = null;
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

        final List<PendingRequestInfoView> transfers = temporalService.getRequests();

        model.addAttribute("transfers", transfers);

        return "transfers"; //navigate to view
    }


    @GetMapping("/pending-transfers")
    public String pendingRequests(Model model) {

        final List<PendingRequestInfoView> pendingRequests = temporalService.getPendingRequests();

        model.addAttribute("pendingRequests", pendingRequests);

        return "pending-transfers"; //navigate to view
    }

    //TODO make post
    @GetMapping("/pending-transfers/{requestId}/{status}")
    public String setTransferStatus(@PathVariable String requestId,
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
            moneyTransferWorkflow.setTransferStatus(transferStatus);

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
        return new ResponseEntity(temporalService.getPendingRequests().size(),
                HttpStatus.OK);
    }


}