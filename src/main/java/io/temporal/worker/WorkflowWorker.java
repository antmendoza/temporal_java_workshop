package io.temporal.worker;

import io.temporal.client.WorkflowClient;
import io.temporal.moneytransferapp.activity.AccountServiceImpl;
import io.temporal.moneytransferapp.activity.BankingClient;
import io.temporal.moneytransferapp.workflow.MoneyTransferWorkflowImpl;
import io.temporal.serviceclient.WorkflowServiceStubs;

import static io.temporal.moneytransferapp.workflow.MoneyTransferWorkflowImpl.TASK_QUEUE;


public class WorkflowWorker {

    public static void main(String[] args) {


        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();


        /*
         * Get a Workflow service client which can be used to start, Signal, and Query Workflow Executions.
         */
        WorkflowClient client = WorkflowClient.newInstance(service);

        /*
         * Define the workflow factory. It is used to create workflow workers for a specific task queue.
         */
        WorkerFactory factory = WorkerFactory.newInstance(client, WorkerFactoryOptions.newBuilder()
                .build());

        /*
         * Define the workflow worker. Workflow workers listen to a defined task queue and process
         * workflows and activities.
         */
        Worker worker = factory.newWorker(TASK_QUEUE, WorkerOptions.newBuilder()
                .build());

        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(new AccountServiceImpl(new BankingClient()));

        factory.start();

    }

}
