# Signal

Exercise: Modify the code in `io.temporal.exercise3.signalworkflow.initial` to require approval for all operation which 
amount is > 1000:
- wait for the transfer to be authorized if amount > 1000.
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
        MoneyTransferWorkflow.class, Starter.MY_BUSINESS_ID, Optional.empty());

workflowStub.approveTransfer(yes);
```


### [Code sample](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloSignal.java) 



