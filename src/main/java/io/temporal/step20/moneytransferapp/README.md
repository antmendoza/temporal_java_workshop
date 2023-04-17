# Signal


https://docs.temporal.io/application-development/features?lang=java#signals

## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step20.moneytransferapp.ClientStartRequest"`

## Start worker
`mvn compile exec:java -Dexec.mainClass="worker.io.temporal.step20.moneytransferapp.WorkflowWorker"`

## Signal workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step20.moneytransferapp.SignalWorkflow"`

