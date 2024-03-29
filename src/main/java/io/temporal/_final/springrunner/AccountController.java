package io.temporal._final.springrunner;

import io.temporal._final.WorkerProcess;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal._final.solution.workflow.child.MoneyTransferWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.*;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;

import static io.temporal._final.WorkerProcess.namespace;

@Controller
public class AccountController {


    final String taskQueue = WorkerProcess.TASK_QUEUE;
    private static WorkflowClient workflowClientExecutionAPI;
    private final List<String> tasks = Arrays.asList("a", "b", "c", "d", "e", "f", "g");

    public AccountController() {

        if(workflowClientExecutionAPI == null){
            final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
            workflowClientExecutionAPI = WorkflowClient.newInstance(service);
        }

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
        final List<AccountInfoView> accounts = workflowClienVisibilityAPI()
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



    @GetMapping("/accounts-new")
    public String newAccountView(Model model) {

        final String accountId = "" + getRandom(1000) + System.currentTimeMillis();
        final String customerId = "" + getRandom(1000) + System.currentTimeMillis();
        final int amount = getRandom(1000);
        Account account = new Account(accountId, customerId, amount);

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

        final AccountWorkflow accountWorkflow = workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class,
                workflowId);

        //Signals are async request to server-> workflow execution
        accountWorkflow.requestTransfer(transferRequest);

        return "redirect:/accounts"; //view
    }


    @GetMapping("/pending-approvals")
    public String pendingApprovals(Model model) {

        // We can query visibility API anytime as long as Temporal server is running
        //
        // Visibility API is eventually consistent, real word application should
        // store data in external db for high throughput and real time data

        // http://localhost:8233/namespaces/default/workflows?query=WorkflowType%3D%22MoneyTransferWorkflow%22+and+ExecutionStatus%3D%22Running%22+and+TransferRequestState%3D%22ApprovalRequired%22
        final String query = "WorkflowType=\"MoneyTransferWorkflow\" and ExecutionStatus=\"Running\" and " +
                "TransferRequestState=\"ApprovalRequired\"";

        final List<PendingApprovalInfoView> pendingApprovals = workflowClienVisibilityAPI()
                .listWorkflowExecutions(ListWorkflowExecutionsRequest.newBuilder()
                        .setQuery(query)
                        .setNamespace(namespace)
                        .build()).getExecutionsList().stream().map(execution -> {


                    final String workflowId = execution.getExecution().getWorkflowId();

                    final TransferRequest transferRequest =
                            workflowClientExecutionAPI.newWorkflowStub(MoneyTransferWorkflow.class, workflowId).getTransferRequest();

                    return new PendingApprovalInfoView(workflowId, transferRequest);
                }).toList();


        model.addAttribute("pendingApprovals", pendingApprovals);

        return "pending-approvals"; //view
    }


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


    private int getRandom(int max) {
        return 1 + ((int) (Math.random() * max));
    }


    private static WorkflowServiceGrpc.WorkflowServiceBlockingStub workflowClienVisibilityAPI() {
        return workflowClientExecutionAPI.getWorkflowServiceStubs()
                .blockingStub();
    }


}