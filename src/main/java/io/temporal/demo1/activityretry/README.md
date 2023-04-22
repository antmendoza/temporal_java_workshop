# Introduction

This example demostrate how Temporal retry activity execution in presence of failure. 

The activity code contains logic to simulate 3 consecutive failures and will success after the 
third attend

## Start workflow
`mvn compile exec:java -Dexec.mainClass="io.temporal.demo1.activityretry.StartRequest"`

## Start worker
`mvn compile exec:java -Dexec.mainClass="io.temporal.demo1.activityretry.WorkerProcess"`

