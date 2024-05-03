# Bank application

During this exercise we are going to create an application using Temporal and Spring Boot.

## During development
My recommendation is to start with a fresh environment for this exercise. Go ahead and delete the file `my_test.db` and 
restart the Temporal Server.

You might need to do the same more than once during this exercise. As you are making changes in your workflows, 
the code will become incompatible with the existing running workflows and you will run into non-deterministic errors. 
Don't worry too much about this right now; just delete the file `my_test.db` and restart the server.

You can refer later to this documentation if you want to learn more:
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
- Users can access account details, like transactions started from the account and the final amount.
- Users can close the account anytime.
  - If there are pending transactions, the request will be rejected.
  - After the account is close, the system send a notification to the customer.


During this exercise we are going to apply the concepts learned on the previous one, plus:
- [Child workflows](https://docs.temporal.io/encyclopedia/child-workflows):
  A Child Workflow Execution is a Workflow Execution that is spawned from within another Workflow in the same Namespace.
- [Custom Search Attributes](https://docs.temporal.io/visibility#custom-search-attributes):
  Search Attributes that are relevant to our business needs, and will allow us to query workflow executions.


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
- [./initial/AccountWorkflow.java](./initial/AccountWorkflow.java): This is the interface of our main workflow. To highlight
  the following methods:
  - `open` is the main workflow method.
  - `requestTransfer` method to request transfer for one account to another account
  - `withdraw` to subtract money from the current account.
  - `deposit` to add money to the current account.

  Each instance of an `AccountWorkflow` will be an account in our system, it is what in Temporal is called Entity Workflow
(doc [here](https://temporal.io/blog/workflows-as-actors-is-it-really-possible) and [here](https://temporal.io/blog/actors-and-workflows-part-2)).

  Their main characteristic is that:
  - they can run forever
  - they can spawn other workflows (Child workflows)
  - they can receive and react to messages (Signals and Updates)

- [./initial/ActivityWithTemporalClient.java](./initial/ActivityWithTemporalClient.java): This is the activity implementation
  that interact with workflow executions.
- [./initial/MoneyTransferWorkflowImpl.java](./initial/MoneyTransferWorkflowImpl.java): Is responsible from moving money from 
one account to another account, it is the same implementation we had in previous exercises. It uses `ActivityWithTemporalClient` to 
`withdraw` and `deposit` money to workflows (accounts).

Spring Boot: 
- [./springrunner/AccountViewController.java](./springrunner/AccountViewController.java) and 
[./springrunner/TransferViewController.java](./springrunner/TransferViewController.java) are REST Controllers that take 
the request and send it to the Temporal Server. From here we will start, query, signal workflows... 

###  Implementation


WIP


### Run the code 


###  Run the code (solution)

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
