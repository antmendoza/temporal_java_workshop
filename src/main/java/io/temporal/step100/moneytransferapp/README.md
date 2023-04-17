# Child workflows


## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step100.moneytransferapp.ClientStartRequest"`


## Start worker
`mvn compile exec:java -Dexec.mainClass="worker.io.temporal.step100.moneytransferapp.WorkflowWorker"`
