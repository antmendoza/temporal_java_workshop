package io.temporal._final.springrunner;

import com.github.javafaker.Faker;
import io.temporal._final.WorkerProcess;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.Account;
import io.temporal.model.AccountSummaryResponse;
import io.temporal.model.CloseAccountResponse;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static io.temporal._final.WorkerProcess.namespace;

@Controller
public class AccountViewController {


    private static Faker fakerInstance;
    private static WorkflowClient workflowClientExecutionAPI;
    final String taskQueue = WorkerProcess.TASK_QUEUE;

    public AccountViewController() {

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

    @GetMapping("/")
    public String defaultView(Model model) {

        return "redirect:/accounts"; //view
    }

    @GetMapping("/accounts")
    public String accountsView(Model model) {

        // We can query visibility API anytime as long as Temporal server is running
        //
        // Visibility API is eventually consistent, real word application should
        // store data in external db for high throughput and real time data

        // http://localhost:8233/namespaces/default/workflows?query=WorkflowType%3D%22AccountWorkflow%22
        final String query = "WorkflowType=\"AccountWorkflow\"";
        final List<AccountInfoView> accounts = workflowClientVisibilityAPI()
                .listWorkflowExecutions(ListWorkflowExecutionsRequest.newBuilder()
                        .setQuery(query)
                        .setNamespace(namespace)
                        .build()).getExecutionsList().stream().map((execution) ->
                {
                    final String workflowId = execution.getExecution().getWorkflowId();

                    //This query is executed by the workers, we need a worker running to query workflow executions
                    final AccountSummaryResponse accountSummary =
                            workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class, workflowId).getAccountSummary();

                    final String status = "WORKFLOW_EXECUTION_STATUS_RUNNING".equals(execution.getStatus().toString()) ? "Open" : "Closed";

                    return new AccountInfoView(workflowId, accountSummary, status);

                }).toList();


        model.addAttribute("accounts", accounts);

        return "accounts"; //view
    }

    @GetMapping("/accounts/new")
    public String newAccountView(Model model) {

        final String accountId = "" + fakerInstance.random().nextInt(100_000, 1_000_000);
        final String customerName = fakerInstance.artist().name();
        final int balance = fakerInstance.random().nextInt(500, 1_000);
        Account account = new Account(accountId, customerName, balance);

        model.addAttribute("account", account);

        return "account-new"; //view
    }

    //TODO PostMapping
    @GetMapping("/accounts/{accountId}/close")
    public String closeAccount(@PathVariable String accountId, Model model) {

        //We need the workflow id to signal it to make the transfer
        final String workflowId = AccountWorkflow.workflowIdFromAccountId(accountId);

        final AccountWorkflow accountWorkflow = workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class,
                workflowId);

        //Signals are async request to server-> workflow execution
        CloseAccountResponse response = accountWorkflow.closeAccount();

        return "redirect:/accounts"; //view
    }

    @PostMapping("/accounts")
    public String createAccount(@ModelAttribute("account") Account account, Model model) {

        final String workflowId = AccountWorkflow.workflowIdFromAccountId(account.accountId());

        final AccountWorkflow accountWorkflow = workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue(taskQueue)
                        .build());

        WorkflowClient.start(accountWorkflow::open, account);


        return "redirect:/accounts"; //view
    }


}