# Workflow task  failure

This example demostrate the workflow task will retry in prosence of errors. 
The workflow code throws runtime error, once we fix the code and redeploy the worker, the worker will continue the workflow execution.

### Make sure Temporal Server is running locally
- https://docs.temporal.io/application-development/foundations#run-a-development-server

### run Starterjava (send request/start workflow)
### run WorkerProcess.java (worker executes our code)

note runtime exceptions in console, your worker keep retrying


### open, MoneyTransferWorkflowImpl.java,
- fix the code
- stop the worker
- start the worker / redeploy your code 

The workflow execution will continue as is nothing has happened 
(method `accountService.withdraw` won’t be executed again)




