# Child workflows


## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step10.moneytransferapp.Starter"`


## Start worker
`mvn compile exec:java -Dexec.mainClass="io.temporal.step10.moneytransferapp.worker.WorkflowWorker"`
