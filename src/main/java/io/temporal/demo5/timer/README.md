# Timer


Exercise: Modify code, in io.temporal.demo5.timer.initial, introduce a timer to sleep the workflow code for 2 minutes, 
between withdraw and deposit methods.


### [Timer](https://docs.temporal.io/application-development/features?lang=java#timers)

- Modify MoneyTransferWorkflowImpl.java and introduce a timer
- Timers are persisted: 
  - Start the workflow StartRequest.java, and the worker WorkerProcess.java, wait 30 seconds, 
  stop the server and start it again. The timer will be fired as expected.

