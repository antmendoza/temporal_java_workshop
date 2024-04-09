package io.temporal._final.springrunner;

import com.github.javafaker.Faker;
import io.temporal._final.WorkerProcess;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.TemporalException;
import io.temporal.model.Account;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static io.temporal._final.springrunner.AccountService.getAccountInfoView;


@Controller
public class AccountViewController {


    private static Faker fakerInstance;
    private static WorkflowClient workflowClientExecutionAPI;
    final String taskQueue = WorkerProcess.TASK_QUEUE;

    public AccountViewController() {

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

    @GetMapping("/")
    public String defaultView(Model model) {

        return "redirect:/accounts"; //navigate to view
    }

    @GetMapping("/accounts")
    public String accountsView(Model model) {

        final List<AccountInfoView> accounts =
                AccountService.getAccounts(workflowClientVisibilityAPI(), workflowClientExecutionAPI);

        model.addAttribute("accounts", accounts);

        return "accounts"; //navigate to view

    }


    @GetMapping("/accounts/{accountId}")
    public String createAccount(@PathVariable String accountId,
                                Model model
            , RedirectAttributes redirectAttrs) {

        try {

            final String workflowId = AccountWorkflow.workflowIdFromAccountId(accountId);

            AccountInfoView account = getAccountInfoView(workflowId, workflowClientExecutionAPI);

            model.addAttribute("account", account);


        } catch (
                TemporalException e) {
            redirectAttrs.addFlashAttribute("msg", e.getCause());
        }

        return "account-info"; //navigate to view

    }


    @PostMapping("/accounts")
    public String createAccount(@ModelAttribute("account") Account account,
                                Model model,
                                RedirectAttributes redirectAttrs) {

        try {

            final String workflowId = AccountWorkflow.workflowIdFromAccountId(account.accountId());

            final AccountWorkflow accountWorkflow = workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class,
                    WorkflowOptions.newBuilder()
                            // workflowId should be our business id
                            .setWorkflowId(workflowId)
                            .setTaskQueue(taskQueue)
                            .build());

            // Start account workflow
            WorkflowClient.start(accountWorkflow::open, account);


        } catch (TemporalException e) {
            redirectAttrs.addFlashAttribute("msg", e.getCause());
        }

        return "redirect:/accounts"; //navigate to view

    }

    @GetMapping("/accounts/new")
    public String newAccountView(Model model) {

        //Dummy values
        final String accountId = "" + fakerInstance.random().nextInt(100_000, 1_000_000);
        final String customerName = fakerInstance.artist().name();
        final int balance = fakerInstance.random().nextInt(500, 1_000);

        final Account account = new Account(accountId, customerName, balance);

        model.addAttribute("account", account);

        return "account-new"; //navigate to view
    }


    //TODO PostMapping
    @GetMapping("/accounts/{accountId}/close")
    public String closeAccount(@PathVariable String accountId,
                               Model model,
                               RedirectAttributes redirectAttrs) {

        try {


            //We need the workflow id to signal it to make the transfer
            final String workflowId = AccountWorkflow.workflowIdFromAccountId(accountId);

            final AccountWorkflow accountWorkflow = workflowClientExecutionAPI.newWorkflowStub(AccountWorkflow.class,
                    workflowId);

            //UpdateWorkflow is a sync request, this code will block until the workflow method returns
            accountWorkflow.closeAccount();
        } catch (TemporalException e) {
            //Update workflow can throw exceptions
            redirectAttrs.addFlashAttribute("msg", e.getCause());
        }

        return "redirect:/accounts"; //navigate to view
    }


}