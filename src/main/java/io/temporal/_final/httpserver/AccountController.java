package io.temporal._final.httpserver;

import io.temporal._final.WorkerProcess;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflowservice.v1.ListOpenWorkflowExecutionsRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.Account;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

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
        final List<AccountInfo> openAccounts = workflowClient.getWorkflowServiceStubs()
                .blockingStub()
                .listOpenWorkflowExecutions(ListOpenWorkflowExecutionsRequest.newBuilder()
                        .setNamespace("default")
                        .build()).getExecutionsList().stream().map((execution) ->
                {
                    final String workflowId = execution.getExecution().getWorkflowId();

                    //This query is executed by our workers, we need a worker running to query a workflow
                    Account account = workflowClient.newWorkflowStub(AccountWorkflow.class, workflowId).getAccount();
                    return new AccountInfo(workflowId, account);

                }).toList();



        model.addAttribute("openAccounts", openAccounts);

        return "accounts"; //view
    }



    @GetMapping("/accounts-new")
    public String newAccountView_(Model model) {

        final String accountId = "" + getRandom(1000) + System.currentTimeMillis();
        final String customerId = "" + getRandom(1000) + System.currentTimeMillis();
        final int amount = getRandom(1000);
        Account account = new Account(accountId,customerId, amount);

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

        model.addAttribute("new-workflowId", workflowId);

        return "redirect:/accounts"; //view
    }


    private int getRandom(int max) {
        return ((int) (Math.random() * max)) ;
    }


}