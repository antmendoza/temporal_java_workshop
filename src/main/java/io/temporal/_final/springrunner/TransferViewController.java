package io.temporal._final.springrunner;

import com.github.javafaker.Faker;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.model.TransferRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TransferViewController {

    private static Faker fakerInstance;

    private static WorkflowClient workflowClientExecutionAPI;

    public TransferViewController() {

        if (workflowClientExecutionAPI == null) {
            final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
            workflowClientExecutionAPI = WorkflowClient.newInstance(service);
        }

        if (fakerInstance == null) {
            fakerInstance = Faker.instance();
        }

    }


    @GetMapping("/transfer-request/{fromAccountId}")
    public String newTransferView(@PathVariable String fromAccountId, Model model) {

        final String toAccountId = "" + fakerInstance.random().nextInt(100_000, 1_000_000);
        final int amount = fakerInstance.random().nextInt(10, 100);
        final TransferRequest transferRequest = new TransferRequest(fromAccountId, toAccountId, amount);

        model.addAttribute("transferRequest", transferRequest);

        return "transfer-request"; //view
    }


    @PostMapping("/transfers")
    public String requestTransfer(@ModelAttribute("transferRequest") TransferRequest transferRequest,
                                  Model model) {


        //We need the workflow id to signal it to make the transfer
        final String workflowId = AccountWorkflow.workflowIdFromAccountId(transferRequest.fromAccountId());

        final AccountWorkflow accountWorkflow = workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class,
                workflowId);

        //Signals are async request to server-> workflow execution
        accountWorkflow.requestTransfer(transferRequest);

        return "redirect:/accounts"; //view
    }


}