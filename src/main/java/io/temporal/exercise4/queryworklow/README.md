# Query


Exercise: Modify the code in `io.temporal.exercise4.queryworklow.initial` in a way that a client can retrieve the transfer status 
(create a method that returns the status of the transaction):
- transfer status can be any of `INITIATED, WAITING_APPROVAL, APPROVED, COMPLETED, DENIED`.

### [Query](https://docs.temporal.io/application-development/features?lang=java#queries)

### [Workflow implementation](https://docs.temporal.io/application-development/features?lang=java#handle-query)

### [Client implementation](https://docs.temporal.io/application-development/features?lang=java#send-query)

- Documentation need to be fixed, use the following code to query a workflow execution:

```
final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

final WorkflowClient client = WorkflowClient.newInstance(service);

final MoneyTransferWorkflow workflowStub =
    client.newWorkflowStub(
        MoneyTransferWorkflow.class, 
        Starter.MY_BUSINESS_ID, 
        Optional.empty());

System.out.println("queryStatus result: " + workflowStub.queryStatus());
```

### [Code sample](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloQuery.java) 

