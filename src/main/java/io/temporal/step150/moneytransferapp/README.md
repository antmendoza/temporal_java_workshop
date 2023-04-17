# Data converters

[https://docs.temporal.io/dataconversion#payload-codec](https://docs.temporal.io/dataconversion#payload-codec)

## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step150.moneytransferapp.ClientStartRequest"`

## Start worker
`mvn compile exec:java -Dexec.mainClass="worker.io.temporal.step15.moneytransferapp.WorkflowWorker"`

# Payload codec
[https://docs.temporal.io/dataconversion#payload-codec](https://docs.temporal.io/dataconversion#payload-codec)

## Start http server
`mvn compile exec:java -Dexec.mainClass="io.temporal.step150.moneytransferapp.HttpServer"`
