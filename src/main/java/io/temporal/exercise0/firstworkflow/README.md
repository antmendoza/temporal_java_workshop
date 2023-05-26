# Introduction

This example guide you, step by step, on how to: 
- build a workflow execution, 
- send a request to the server to start it,
- and implement a worker in charge to execute your code. 



## Exercise 1: Workflow without activity invocation (final implementation in package solution1)

### Workflow and implementation

Steps to create a Temporal application with Java:

- [create a workflow interface](https://docs.temporal.io/application-development/foundations?lang=java#develop-workflows)

```
/** Workflow interface has to have at least one method annotated with @WorkflowMethod. */
@WorkflowInterface
public interface MoneyTransferWorkflow {

  // The Workflow method is called by the initiator either via code or CLI.
  @WorkflowMethod
  String transfer(TransferRequest transferRequest);
}
```

- workflow implementation
```
public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

  @Override
    public String transfer(TransferRequest transferRequest) {

        System.out.println("My first workflow " + transferRequest);

        return "done";
    }
}

```


### Make sure Temporal Server is running locally
- https://docs.temporal.io/application-development/foundations#run-a-development-server

### Implement the worker


- [Run dev worker](https://docs.temporal.io/application-development/foundations?lang=java#run-a-dev-worker)
```

public class WorkerProcess {

  static final String TASK_QUEUE = WorkerProcess.class.getPackageName() + ":" + "MoneyTransfer";

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
    WorkerFactory factory =
        WorkerFactory.newInstance(client, WorkerFactoryOptions.newBuilder().build());

    /*
     * Define the workflow worker.
     * Workflow workers listen to a defined task queue and process workflows and activities.
     */
    Worker worker = factory.newWorker(TASK_QUEUE, WorkerOptions.newBuilder().build());

    worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);

    factory.start();
  }
}

```

### Implement workflow client, and instantiate workflow execution (run the main method of this class)

- [start workflow execution](https://docs.temporal.io/application-development/foundations?lang=java#start-workflow-execution)

```
public class Starter {

    static final String MY_BUSINESS_ID = io.temporal.exercise0.firstworkflow.solution1.Starter.class.getPackageName() + ":money-transfer";

    public static void main(String[] args) {

        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        final WorkflowClient client = WorkflowClient.newInstance(service);

        final WorkflowOptions options =
                WorkflowOptions.newBuilder()
                        .setWorkflowId(MY_BUSINESS_ID)
                        .setTaskQueue(WorkerProcess.TASK_QUEUE)
                        .build();

        // Create the workflow client stub.
        // It is used to start our workflow execution.
        final MoneyTransferWorkflow workflow =
                client.newWorkflowStub(MoneyTransferWorkflow.class, options);

        TransferRequest transferRequest =
                new TransferRequest("fromAccount", "toAccount", "referenceId", 200);
        // Sync, blocking invocation
        // workflow.transfer(transferRequest);

        // Async
        WorkflowClient.start(workflow::transfer, transferRequest);
        // block and wait execution to finish
        String result = client.newUntypedWorkflowStub(MY_BUSINESS_ID).getResult(String.class);
        System.out.println("Result " + result);
    }
}

```

- navigate to the UI (http://localhost:8233 or http://localhost:8080) and note workflow is not making progress, 
there are no workers polling workflow/activity tasks.



### Start the worker (run WorkerProcess main method)

Once you start the worker, it will poll tasks from the server and execute your workflow code.


> You will find the full implementation in `io.temporal.exercise0.firstworkflow.solution1`


## Exercise 2: Add activity invocation (final implementation in package solution2)

### Create activity interfaz and implementation

```
@ActivityInterface
public interface AccountService {

  @ActivityMethod
  void deposit(DepositRequest depositRequest);

  @ActivityMethod
  void withdraw(WithdrawRequest withdrawRequest);
}

```

```
public class AccountServiceImpl implements AccountService {

    private final Logger log = Workflow.getLogger(AccountServiceImpl.class.getSimpleName());

    private final BankingClient bankingClient;

    public AccountServiceImpl(BankingClient bankingClient) {
        this.bankingClient = bankingClient;
    }

    @Override
    public void withdraw(WithdrawRequest withdrawRequest) {
        log.info("Init withdraw : " + withdrawRequest);
        this.bankingClient.withdraw(withdrawRequest);
        log.info("End withdraw : " + withdrawRequest);
    }

    @Override
    public void deposit(DepositRequest depositRequest) {
        log.info("Init deposit : " + depositRequest);
        this.bankingClient.deposit(depositRequest);
        log.info("End deposit : " + depositRequest);
    }

}

```

### Add activity invocation to your workflow code

- Create activity stub.
```
 final AccountService accountService =
          Workflow.newActivityStub(
                  AccountService.class,
                  ActivityOptions.newBuilder()
                          .setStartToCloseTimeout(Duration.ofSeconds(3))
                          .build());
```

### implement workflow code, invoke your activity


- add activity invocation/s to workflow code
```


  private final Logger log = Workflow.getLogger(workflow.io.temporal.exercise1.activityretry.MoneyTransferWorkflowImpl.class.getSimpleName());

  @Override
  public String transfer(TransferRequest transferRequest) {
    log.info("Init transfer: " + transferRequest);

    accountService.withdraw(
            new WithdrawRequest(
                    transferRequest.fromAccountId(),
                    transferRequest.referenceId(),
                    transferRequest.amount()));

    accountService.deposit(
            new DepositRequest(
                    transferRequest.toAccountId(),
                    transferRequest.referenceId(),
                    transferRequest.amount()));

    log.info("End transfer: " + transferRequest);

    return "done";
  }
```

### Register activity/s implementation with worker and restart worker

```
    worker.registerActivitiesImplementations(new AccountServiceImpl(new BankingClient()));
```

### Start new workflow execution


Your worker will poll and execute workflow and activity tasks:

```
21:30:43.007 {io.temporal.exercise0.firstworkflow.solution2:money-transfer} INFO  AccountServiceImpl - Init withdraw : WithdrawRequest[accountId=fromAccount, referenceId=referenceId, amount=200.0] 
Withdraw init: WithdrawRequest[accountId=fromAccount, referenceId=referenceId, amount=200.0]
Withdraw end: WithdrawRequest[accountId=fromAccount, referenceId=referenceId, amount=200.0]
21:30:43.795 {io.temporal.exercise0.firstworkflow.solution2:money-transfer} INFO  AccountServiceImpl - End withdraw : WithdrawRequest[accountId=fromAccount, referenceId=referenceId, amount=200.0] 
21:30:48.942 {io.temporal.exercise0.firstworkflow.solution2:money-transfer} INFO  AccountServiceImpl - Init deposit : DepositRequest[accountId=toAccount, referenceId=referenceId, amount=200.0] 
Deposit init: DepositRequest[accountId=toAccount, referenceId=referenceId, amount=200.0]
Deposit end: DepositRequest[accountId=toAccount, referenceId=referenceId, amount=200.0]
21:30:49.294 {io.temporal.exercise0.firstworkflow.solution2:money-transfer} INFO  AccountServiceImpl - End deposit : DepositRequest[accountId=toAccount, referenceId=referenceId, amount=200.0] 
21:30:49.322 {io.temporal.exercise0.firstworkflow.solution2:money-transfer} INFO  MoneyTransferWorkflowImpl - End transfer: TransferRequest[fromAccountId=fromAccount, toAccountId=toAccount, referenceId=referenceId, amount=200.0] 

```
