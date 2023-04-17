# Introduction


This example demostrate how Temporal save workflow state internally in presence of error and, after fixing the error, 
the workflow will continue with the execution.

## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.step0.moneytransferapp.ClientStartRequest"`


## Start worker
`mvn compile exec:java -Dexec.mainClass="io.temporal.step0.moneytransferapp.Worker"`


## Fix error
Uncomment exception thrown in AccountServiceImpl.deposit

## Restart worker


