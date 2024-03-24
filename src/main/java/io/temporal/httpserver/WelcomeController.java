package io.temporal.httpserver;

import io.temporal._final.WorkerProcess;
import io.temporal._final.solution.workflow.AccountWorkflow;
import io.temporal.api.workflowservice.v1.ListOpenWorkflowExecutionsRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.model.Account;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class WelcomeController {


    final String taskQueue = WorkerProcess.TASK_QUEUE;
    private final WorkflowClient workflowClient;
    private final String message = "test";
    private final List<String> tasks = Arrays.asList("a", "b", "c", "d", "e", "f", "g");

    public WelcomeController() {

        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        workflowClient = WorkflowClient.newInstance(service);

    }

    @GetMapping("/")
    public String main(Model model) {

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


        model.addAttribute("message", message);

        model.addAttribute("openAccounts", openAccounts);

        return "welcome"; //view
    }

    // /hello?name=kotlin
    @GetMapping("/hello")
    public String mainWithParam(
            @RequestParam(name = "name", required = false, defaultValue = "")
            String name, Model model) {

        model.addAttribute("message", name);

        return "welcome"; //view
    }

}