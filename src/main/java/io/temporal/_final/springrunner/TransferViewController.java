package io.temporal._final.springrunner;

import com.github.javafaker.Faker;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.failure.TemporalException;
import io.temporal.model.RequestTransferResponse;
import io.temporal.model.TransferRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

            redirectAttrs.addFlashAttribute("msg", "Request created with operation id " +
                    response.getOperationId());


        } catch (TemporalException e) {
            redirectAttrs.addFlashAttribute("msg", e.getCause());
        }

        return "redirect:/accounts"; //navigate to view

    }


}