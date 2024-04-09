package io.temporal._final;

import io.temporal._final.solution.activity.AccountServiceWithTemporalClient;
import io.temporal._final.solution.workflow.AccountWorkflowImpl;
import io.temporal._final.solution.workflow.child.AccountCleanUpWorkflowImpl;
import io.temporal._final.solution.workflow.child.MoneyTransferWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.activity.NotificationServiceImpl;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import io.temporal.worker.WorkerOptions;


public class WorkerProcess {

    public static final String TASK_QUEUE = WorkerProcess.class.getPackageName() + ":" + "MoneyTransfer";


    public static void main(String[] args) {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .setTarget(io.temporal.Constants.targetGRPC)
                .build());

        /*
         * Get a Workflow service client which can be used to start, Signal, and Query Workflow Executions.
         */
        final WorkflowClient client = WorkflowClient.newInstance(service, WorkflowClientOptions
                .newBuilder()
                .setNamespace(io.temporal.Constants.namespace)
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
                factory.newWorker(TASK_QUEUE, WorkerOptions.newBuilder().build());

        worker.registerWorkflowImplementationTypes(
                AccountWorkflowImpl.class, MoneyTransferWorkflowImpl.class, AccountCleanUpWorkflowImpl.class);
        worker.registerActivitiesImplementations(new AccountServiceWithTemporalClient(client));
        worker.registerActivitiesImplementations(new NotificationServiceImpl());

        factory.start();
    }
}
