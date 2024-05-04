# Bank application

During this exercise, we are going to create an application to manage accounts and transfers using Temporal and Spring Boot.


## During development
Start with a fresh environment. Delete the file `my_test.db` and restart the Temporal Server.

You might need to do the same more than once during this exercise. As you are making changes in your workflows, 
the code will become incompatible with the existing running workflows and you will run into non-deterministic errors. 
Don't worry too much about this right now; delete the file `my_test.db` and restart the server or start it without persisting 
the server state (remove the flag `--db-filename "${FILE_DB}"` to start the server)

You can refer later to this documentation if you want to learn more about `Non Deterministic Errors` and versioning:
- [Deterministic constraints](https://docs.temporal.io/workflows#deterministic-constraints)
- [Intrinsic non-deterministic logic](https://docs.temporal.io/dev-guide/java/durable-execution#intrinsic-non-deterministic-logic)
- [Non-deterministic code changes](https://docs.temporal.io/dev-guide/java/durable-execution#durability-through-replays)
- [Versioning - Java SDK feature guide](https://docs.temporal.io/dev-guide/java/versioning)
- [Move Fast WITHOUT Breaking Anything - Workflow Versioning with Temporal](https://www.youtube.com/watch?v=kkP899WxgzY)


## Exercise

The application simulates a bank with the following features:

- Users can create accounts.
- Users can send money from one account to another account.
  - If the amount to transfer is > 100, the operation needs to be approved.
- Users can access account details, like transactions started from the account and the current amount.
- Users can close the account anytime.
  - If there are pending transactions, the request will be rejected.
  - After the account is close, the system send a notification to the customer.


"During this exercise, we are going to apply the concepts learned in the previous one, and add:
- [Child workflows](https://docs.temporal.io/encyclopedia/child-workflows):
  A Child Workflow Execution is a Workflow Execution that is spawned from within another Workflow.
- [Custom Search Attributes](https://docs.temporal.io/visibility#custom-search-attributes):
  Search Attributes are searchable field, we can add SA that are relevant to our business needs to query workflow executions.

This folder contains two main sub-folders:
- `initial` is you starting point, the code skeleton within which you must work to complete the exercise following the steps described below..
- `solution` contains the final code, after all steps are implemented.
and 
- `springrunner` that contains REST Controllers and the SpringBootApplication class.
- We use thymeleaf for the view, you can find the views in `src/main/resources/templates`. You will notice that our UI has links
 `(View in Temporal UI)` to help relate what is shown in it with the Temporal UI.

![img.png](img.png)


Begin by working with the code in the `initial` folder. Take your time to familiarize yourself with the following pieces of code:

Temporal:
- [./AccountWorkflow.java](./AccountWorkflow.java): This is the interface of our main workflow. To highlight
  the following methods:
  - `open` is the main workflow method.
  - `requestTransfer` method to request transfer for one account to another account
  - `withdraw` to subtract money from the current account.
  - `deposit` to add money to the current account.

  Each instance of an `AccountWorkflow` represent an account in our system, it is what in Temporal is called Entity Workflow
(doc [here](https://temporal.io/blog/workflows-as-actors-is-it-really-possible) and [here](https://temporal.io/blog/actors-and-workflows-part-2)). 
Their main characteristics are:
  - they can run forever
  - they can spawn other workflows (Child workflows)
  - they can receive and react to messages (Signals and Updates)

- [./initial/ActivityWithTemporalClient.java](./initial/ActivityWithTemporalClient.java): Contains the activities to interact with workflow executions
to deposit and withdraw money. 
- [./initial/MoneyTransferWorkflowImpl.java](./initial/MoneyTransferWorkflowImpl.java): Is responsible from moving money from 
one account to another account, it is the same implementation we had in previous exercises. It uses `ActivityWithTemporalClient` to 
`withdraw` and `deposit` money to workflows (accounts).

Spring Boot: 
- [./springrunner/AccountViewController.java](./springrunner/AccountViewController.java) and 
[./springrunner/TransferViewController.java](./springrunner/TransferViewController.java) are REST Controllers that take 
the request and send it to the Temporal Server. From here we will start, query, signal workflows... 

###  Implementation


#### Users can create accounts.

- Open [./initial/AccountWorkflowImpl.java](initial/AccountWorkflowImpl.java) and add a basic implementation to the main method.

The main method is empty.
```
@Override
public void open(final Account account) {    
}

```

If we run the code now, the workflow will close immediately after starting (you can test it), but we want the accounts 
workflows to be long-running, allowing interaction. 

Set the value `account` passed to the method as instance variables and block `Workflow.await` until `closeAccount` becomes true:
```
    @Override
    public void open(final Account account) {

        log.info("Account created " + account);
        this.account = account;
        Workflow.await(() -> closeAccount);
        
    }
```

Now the workfow will be running until the `Workflow.await` condition is true.

Note that `this.account` is used by `getAccountSummary` method to return the account info shown in the table.

Let's create some accounts in our system.

- [Run the code initial folder](./run-the-code-initial-folder.md)
  - Navigate to [http://localhost:3030/accounts](http://localhost:3030/accounts), the UI shows a list(empty) of 
accounts. 
  - Click `New Account`, it will show a form with dummy data (feel free to modify it), once the form is ready submit the information.
  - Now we have one account in our system
  ![img_1.png](img_1.png)
  - if you click `View in Temporal UI` it will take you to the Temporal UI showing the workflow in running state.


#### Users can send money from one account to another account.

- **If the amount to transfer is > 100, the operation needs to be approved**.

#### Users can access account details, like transactions started from the account and the current amount.

#### Users can close the account anytime.
 
- **If there are pending transactions, the request will be rejected.**

- **After the account is close, the system send a notification to the customer.**

### Run the code (solution)


###  Run the code 

- Ensure you have everything you need to run the code, and the Temporal Server is running.
  See [prepare-your-environment.md](./../../../../../../../../prepare-your-environment.md).

- Stop any processes that are running as part of other exercises (such as workers), except the Temporal Server.

- Start the Worker process

```bash
# Go to the root directory
cd ./../../../../../../../
# from the root directory execute
 ./mvnw compile exec:java -Dexec.mainClass="io.temporal.workshop._final.solution.WorkerProcess"

```

- Start the Spring Boot application

```bash
# Go to the root directory
cd ./../../../../../../../
# from the root directory execute
 ./mvnw compile exec:java -Dexec.mainClass="io.temporal.workshop._final.springrunner.Application"

```

Navigate to  [http://localhost:3030/](http://localhost:3030/), where our application exposes the UI.
