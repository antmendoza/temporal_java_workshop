# Signal

Exercise: Modify the code in `io.temporal.demo3.signalworkflow.initial` to wait authorization if the amount > 1000.
- send a notification (new activity) to the client when the operation is completed.
- if the operation is not authorized send a notification (new activity) to the client.

[Signal](https://docs.temporal.io/application-development/features?lang=java#signals)

[Signal implementation](https://docs.temporal.io/application-development/features?lang=java#handle-signal)

[Client implementation](https://docs.temporal.io/application-development/features?lang=java#send-signal-from-client)

- Documentation need to be fixed, use the following code to signal a workflow execution:

```
final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

final WorkflowClient client = WorkflowClient.newInstance(service);

final MoneyTransferWorkflow workflowStub =
    client.newWorkflowStub(
        MoneyTransferWorkflow.class, StartRequest.MY_BUSINESS_ID, Optional.empty());

workflowStub.approveTransfer(yes);
```


### [Code sample](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloSignal.java) 



