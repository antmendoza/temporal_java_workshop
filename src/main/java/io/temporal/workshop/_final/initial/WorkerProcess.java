package io.temporal.workshop._final.initial;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import io.temporal.worker.WorkerOptions;
import io.temporal.workshop.Constants;
import io.temporal.workshop._final.TASK_QUEUE;
import io.temporal.workshop.activity.NotificationServiceImpl;



public class WorkerProcess {



    public static void main(String[] args) {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(Constants.targetGRPC)
                .build());

        /*
         * Get a Workflow service client which can be used to start, Signal, and Query Workflow Executions.
         */
        final WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions
                .newBuilder()
                .setNamespace(Constants.namespace)
                .build());

        /*
         * Define the workflow factory. It is used to create workflow workers for a specific task queue.
         */
        WorkerFactory factory =
                WorkerFactory.newInstance(client, WorkerFactoryOptions.newBuilder().build());

        /*
         * Define the workflow worker. Workflow workers listen to a defined task queue and process
         * workflows and activities.
         */
        Worker worker =
                factory.newWorker(TASK_QUEUE.name, WorkerOptions.newBuilder().build());

        worker.registerWorkflowImplementationTypes(
                AccountWorkflowImpl.class, MoneyTransferWorkflowImpl.class, AccountCleanUpWorkflowImpl.class);
        worker.registerActivitiesImplementations(new ActivityWithTemporalClient(client));
        worker.registerActivitiesImplementations(new NotificationServiceImpl());

        factory.start();
    }
}
