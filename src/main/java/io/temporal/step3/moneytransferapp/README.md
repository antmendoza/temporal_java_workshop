# Query


https://docs.temporal.io/application-development/features?lang=java#queries

## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step2.moneytransferapp.Starter"`

## Start worker
`mvn compile exec:java -Dexec.mainClass="io.temporal.step2.moneytransferapp.worker.WorkflowWorker"`

## Query workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step2.moneytransferapp.QueryWorkflow"`

## Signal workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step2.moneytransferapp.SignalWorkflow"`


