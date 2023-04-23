# Workflow task  failure

This example demostrate how if the workflow code throws runtime error, 
the workflow task will retry until it eventually success.

### Start cluster:
`temporal server start-dev --f java_workshop`

### run StartRequest.java (send request/start workflow)
### run WorkerProcess.java (worker executes our code)

note runtime exceptions in console, your worker keep retrying


### open, MoneyTransferWorkflowImpl.java,
- fix the code
- stop the worker
- start the worker / redeploy your code 

The workflow execution will continue as is nothing has happened 
(method `accountService.withdraw` won’t be executed again)




