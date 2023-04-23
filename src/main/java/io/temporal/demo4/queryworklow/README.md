# Query


Exercise: Modify code, in io.temporal.demo4.queryworklow.initial, introduce a method that returns the authorization status

### [Query](https://docs.temporal.io/application-development/features?lang=java#queries)

### [Workflow implementation](https://docs.temporal.io/application-development/features?lang=java#handle-query)

- Modify MoneyTransferWorkflow.java (to declare query method @QueryMethod) and MoneyTransferWorkflowImpl.java to implement it.

### [Client implementation](https://docs.temporal.io/application-development/features?lang=java#send-query)

- Documentation need to be fixed, use the following code to query a workflow execution:

```
final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

final WorkflowClient client = WorkflowClient.newInstance(service);

final MoneyTransferWorkflow workflowStub =
    client.newWorkflowStub(
        MoneyTransferWorkflow.class, 
        StartRequest.MY_BUSINESS_ID, 
        Optional.empty());

System.out.println("queryStatus result: " + workflowStub.queryStatus());
```

### [Sample code](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloQuery.java) 

