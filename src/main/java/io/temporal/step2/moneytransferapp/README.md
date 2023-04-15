# Signal


https://docs.temporal.io/application-development/features?lang=java#signals

## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step2.moneytransferapp.Starter"`

## Start worker
`mvn compile exec:java -Dexec.mainClass="io.temporal.step2.moneytransferapp.worker.WorkflowWorker"`

## Signal workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step2.moneytransferapp.SignalWorkflow"`

