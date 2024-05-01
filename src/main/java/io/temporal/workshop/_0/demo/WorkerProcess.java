package io.temporal.workshop._0.demo;

import io.temporal.workshop.Constants;
import io.temporal.workshop.activity.NotificationServiceImpl;
import io.temporal.workshop.service.BankingClient;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import io.temporal.worker.WorkerOptions;

public class WorkerProcess {

    static final String TASK_QUEUE = WorkerProcess.class.getPackageName() + ":" + "MoneyTransfer";

    public static void main(String[] args) {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(Constants.targetGRPC)
                .build());

        /*
         * Get a Workflow service client which can be used to start, Signal, and Query Workflow Executions.
         */
        WorkflowClient client = WorkflowClient.newInstance(service);

        /*
         * Define the workflow factory. It is used to create workflow workers for a specific task queue.
         */
        WorkerFactory factory =
                WorkerFactory.newInstance(client, WorkerFactoryOptions.newBuilder().build());

        /*
         * Define the workflow worker.
         * Workflow workers listen to a defined task queue and process workflows and activities.
         */
        Worker worker = factory.newWorker(TASK_QUEUE, WorkerOptions.newBuilder().build());

        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(new AccountServiceImplWithRuntimeException(new BankingClient()));
        worker.registerActivitiesImplementations(new NotificationServiceImpl());

        factory.start();
    }
}
