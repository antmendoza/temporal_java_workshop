package io.temporal._final.httpserver;

import io.temporal._final.WorkerProcess;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflowservice.v1.ListOpenWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.Account;
import io.temporal.model.AccountSummaryResponse;
import io.temporal.model.Transfer;
import io.temporal.model.TransferRequest;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class AccountController {


    final String taskQueue = WorkerProcess.TASK_QUEUE;
    private final WorkflowClient workflowClient;
    private final String message = "test";
    private final List<String> tasks = Arrays.asList("a", "b", "c", "d", "e", "f", "g");

    public AccountController() {

        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        workflowClient = WorkflowClient.newInstance(service);

    }


    @GetMapping("/")
    public String defaultView(Model model) {
        return accountsView(model); //view
    }


    @GetMapping("/accounts")
    public String accountsView(Model model) {

        // We can query visibility API anytime as long as Temporal server is running
        //
        // Visibility API is eventually consistent, real word application should
        // store data in external db for high throughput and real time data

        final List<AccountInfo> accounts = workflowClient.getWorkflowServiceStubs()
                .blockingStub()
                .listWorkflowExecutions(ListWorkflowExecutionsRequest.newBuilder()
                        .setQuery("WorkflowType=\"AccountWorkflow\"")
                        .setNamespace("default")
                        .build()).getExecutionsList().stream().map((execution) ->
                {
                    final String workflowId = execution.getExecution().getWorkflowId();

                    //This query is executed by the workers, we need a worker running to query workflow executions
                    AccountSummaryResponse accountSummary = workflowClient.newWorkflowStub(AccountWorkflow.class, workflowId).getAccountSummary();

                    return new AccountInfo(workflowId, accountSummary);

                }).toList();


        model.addAttribute("accounts", accounts);

        return "accounts"; //view
    }


    @GetMapping("/accounts-new")
    public String newAccountView(Model model) {

        final String accountId = "" + getRandom(1000) + System.currentTimeMillis();
        final String customerId = "" + getRandom(1000) + System.currentTimeMillis();
        final int amount = getRandom(1000);
        Account account = new Account(accountId, customerId, amount);

        model.addAttribute("account", account);

        return "account-new"; //view
    }


    @PostMapping("/accounts")
    public String createAccount(@ModelAttribute("account") Account account, Model model) {

        final String workflowId = AccountWorkflow.workflowIdFromAccountId(account.accountId());

        final AccountWorkflow accountWorkflow = workflowClient.newWorkflowStub(AccountWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(taskQueue)
                        .build());

        final WorkflowExecution workflow = WorkflowClient.start(accountWorkflow::open,
                account);


        return "redirect:/accounts"; //view
    }


    ///



    @GetMapping("/transfer-request/{accountId}")
    public String newTransferView(@PathVariable String accountId, Model model) {

        final String toAccountId = "" + getRandom(1000) + System.currentTimeMillis();
        final int amount = getRandom(10);
        TransferRequest transferRequest = new TransferRequest(accountId, toAccountId, amount);

        model.addAttribute("transferRequest", transferRequest);

        return "transfer-request"; //view
    }



    @PostMapping("/transfers")
    public String requestTransfer(@ModelAttribute("transferRequest") TransferRequest transferRequest,
                                  Model model) {


        //We need the workflow id to signal it to make the transfer
        final String workflowId = AccountWorkflow.workflowIdFromAccountId(transferRequest.fromAccountId());

        final AccountWorkflow accountWorkflow = workflowClient.newWorkflowStub(AccountWorkflow.class,
                workflowId);

        //Signals are async request to server-> workflow execution
        accountWorkflow.requestTransfer(transferRequest);

        return "redirect:/accounts"; //view
    }


    private int getRandom(int max) {
        return ((int) (Math.random() * max));
    }


}