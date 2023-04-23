# Signal

Exercise: Modify code, in io.temporal.demo3.signalworkflow.initial, to require/wait authorization 
(signal) if the amount > 1000

[Signal](https://docs.temporal.io/application-development/features?lang=java#signals)

[Signal implementation](https://docs.temporal.io/application-development/features?lang=java#handle-signal)

- Modify MoneyTransferWorkflow.java (to declare signal method @SignalMethod) and MoneyTransferWorkflowImpl.java to implement it.
- Modify MoneyTransferWorkflowImpl.java to implement “amount > 1000 wait (use `Workflow.await`) authorization”


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
### [Sample code](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloSignal.java) 



