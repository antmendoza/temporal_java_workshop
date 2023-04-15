# Data converters

[https://docs.temporal.io/dataconversion#payload-codec](https://docs.temporal.io/dataconversion#payload-codec)

## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step11.moneytransferapp.Starter"`

## Start worker
`mvn compile exec:java -Dexec.mainClass="io.temporal.step11.moneytransferapp.worker.WorkflowWorker"`

# Payload codec
[https://docs.temporal.io/dataconversion#payload-codec](https://docs.temporal.io/dataconversion#payload-codec)

## Start http server
`mvn compile exec:java -Dexec.mainClass="io.temporal.step11.moneytransferapp.HttpServer"`
