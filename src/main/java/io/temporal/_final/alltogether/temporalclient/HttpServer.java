package io.temporal._final.alltogether.temporalclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.temporal._final.alltogether.WorkerProcess;
import io.temporal._final.alltogether.solution.workflow.AccountWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;
import io.temporal.model.Account;
import io.temporal.model.StartWorkflowResponse;
import io.temporal.model.Transfer;
import io.temporal.model.UpdateCustomerResponse;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import static com.google.gson.JsonParser.parseReader;

public class HttpServer {

    private static final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public static void main(String[] args) throws Exception {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions
                        .newBuilder()
                        .setTarget("127.0.0.1:7233")
                        .build());

        final WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions
                .newBuilder()
                .setNamespace("default")
                .build());


        final int port = 8000;
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/accounts-close", new AccountHandler(client));
        server.createContext("/accounts", new AccountHandler(client));
        server.createContext("/transfer", new MoneyTransferHandler(client));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public static boolean checkRequestMethod(final HttpExchange t, final String currentMethod, final List<String> allowedMethods) throws IOException {


        if (!allowedMethods.contains(currentMethod)) {

            String response = "Wrong method, expected = " + allowedMethods;
            t.sendResponseHeaders(403, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return true;
        }
        return false;
    }

    private static JsonObject getJsonObject(final HttpExchange t) {
        final JsonElement element = parseReader(
                new InputStreamReader(t.getRequestBody()));
        final JsonObject customerAsJson = element.getAsJsonObject();
        return customerAsJson;
    }

    static class AccountHandler implements HttpHandler {
        private final WorkflowClient workflowClient;

        public AccountHandler(final WorkflowClient workflowClient) {
            this.workflowClient = workflowClient;
        }


        @Override
        public void handle(HttpExchange t) throws IOException {

            final String path = t.getRequestURI().getPath();


            System.out.println("path => " + path);


            final String currentMethod = t.getRequestMethod();
            if (checkRequestMethod(t, currentMethod, List.of("PUT"))) return;

            final JsonObject customerAsJson = getJsonObject(t);

            final String customerId = customerAsJson.get("customer-id").getAsString();


            final String taskQueue = WorkerProcess.TASK_QUEUE;


            String response = "";
            int rCode = 200;

            try {

                if (path.endsWith("accounts")) {
                    final String accountId = customerAsJson.get("account-id").getAsString();
                    final double amount = customerAsJson.get("amount").getAsDouble();


                    final String workflowId = AccountWorkflow.workflowIdFromAccountId(accountId);
                    // Start the workflow execution
                    AccountWorkflow accountWorkflow = workflowClient.newWorkflowStub(AccountWorkflow.class,
                            WorkflowOptions.newBuilder()
                                    .setWorkflowId(workflowId)
                                    .setTaskQueue(taskQueue)
                                    .build());
                    try {

                        WorkflowExecution workflow = WorkflowClient.start(accountWorkflow::open,
                                new Account(accountId, customerId, amount));


                        response = ow.writeValueAsString(new StartWorkflowResponse(workflowId, workflow.getRunId()));

                    } catch (WorkflowExecutionAlreadyStarted executionAlreadyStarted) {
                        //do nothing if the workflow is already running
                    }


                } else if (path.endsWith("accounts-close")) {

                    //Update account
                    final String accountId = customerAsJson.get("account-id").getAsString();


                    // Start the workflow execution
                    AccountWorkflow accountWorkflow = workflowClient.newWorkflowStub(AccountWorkflow.class,
                            AccountWorkflow.workflowIdFromAccountId(accountId));// business id

                    accountWorkflow.closeAccount();
                } else {
                    //Update account
                    final String accountId = path.substring("/accounts/".length());


                    // Start the workflow execution
                    AccountWorkflow accountWorkflow = workflowClient.newWorkflowStub(AccountWorkflow.class,
                            AccountWorkflow.workflowIdFromAccountId(accountId));// business id

                    UpdateCustomerResponse result = accountWorkflow.updateCustomer(customerId);
                    response = ow.writeValueAsString(result);
                }

            } catch (Exception e) {
                e.printStackTrace();
                response = e.getCause().getMessage();
                rCode = 400;
            }


            t.sendResponseHeaders(rCode, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class MoneyTransferHandler implements HttpHandler {
        private final WorkflowClient workflowClient;

        public MoneyTransferHandler(final WorkflowClient workflowClient) {
            this.workflowClient = workflowClient;
        }


        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("Update account ");

            final JsonObject customerAsJson = getJsonObject(t);

            final String accountId = customerAsJson.get("account-id").getAsString();
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();


            final String currentMethod = t.getRequestMethod();
            if (checkRequestMethod(t, currentMethod, List.of("PUT"))) return;


            final String workflowId = AccountWorkflow.workflowIdFromAccountId(accountId);
            final String taskQueue = WorkerProcess.TASK_QUEUE;


            String response = "";
            int rCode = 200;

            try {

                //Update account

                // Start the workflow execution
                AccountWorkflow accountWorkflow = workflowClient.newWorkflowStub(AccountWorkflow.class,
                        workflowId);
                final Transfer otherAccount = new Transfer("requeset-id", "otherAccount", 123);
                accountWorkflow.requestTransfer(otherAccount);
                response = ow.writeValueAsString(otherAccount);


            } catch (Exception e) {
                e.printStackTrace();
                response = e.getCause().getMessage();
                rCode = 400;
            }


            t.sendResponseHeaders(rCode, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}