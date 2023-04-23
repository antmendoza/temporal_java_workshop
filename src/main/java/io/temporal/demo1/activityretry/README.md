# Introduction

This example demostrate how Temporal retry activity execution in presence of errors. 

The activity code contains logic to simulate 3 consecutive failures and will success after the 
third attend.

```
  private void simulateServiceIsDownAndSuccessAfterNumIteractions(int numIterationsBeforeSuccess) {

    int attend = Activity.getExecutionContext().getInfo().getAttempt();
    if (attend <= numIterationsBeforeSuccess) {
      String message = "Error: Can not reach service...attend " + attend;
      throw new RuntimeException(message);
    }
  }

```


### Start cluster:
`temporal server start-dev --f java_workshop`

### run StartRequest.java (send request/start workflow)
### run WorkerProcess.java (worker executes our code)

The activity will fail and after the third attend will success. This is the default behavior of activity execution,
you can configure the retry policy through activity options.


