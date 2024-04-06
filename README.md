# Introduction

## What is temporal?




Temporal is a durable execution system ....

//TODO Diagrams and stuff...
//TODO start with update workflow


## Prerequisites

Temporal provides a lightweight server for development (called temporal cli), 
that contains the server itself plus the UI and an in-memory DB.

You can install cli from https://docs.temporal.io/cli#install
Other option is to use docker-compose (https://github.com/temporalio/docker-compose)


For this exersice we will require temporal running and to run a extra command to create a Custom Search Attribute (that 
will allow search workflows matching a specific key/value)




`temporal server start-dev --dynamic-config-value frontend.enableUpdateWorkflowExecution=true --db-filename my_test.db`
- http://localhost:8233/


Open other terminal an run the following command to create the search attribute
`temporal operator search-attribute create --namespace default --type Keyword --name TransferRequestState`


## Modules:

### My first workflow

Two main building blocks, workflows and activities:

- firstworkflow

### What the f** is a durable execution?

.. Temporal will ensure your workflow is executed `at least once`, and will recover ...

#### workflowtaskfretry

#### activityretry

### Communicating with your workflow execution?

#### Signals

(Mention signal with start)

#### Queries

#### Update

### Entity / Actor workflow

- //TODO link post from our blog
  Introduction to Entity workflow...
- the can create

### Putting all together

### Introduction to Temporal

This is a Temporal workshop to introduce the main Temporal primitives:

- Workflow
- Activity
- Signal
- Query
- Update
- ChildWorkflows
- Timers

## env setup

[env-setup.md](./env-setup.md)

## Resources:

### Java Workshops:

- https://www.youtube.com/watch?v=VoSiIwkvuX0 (Part 1)
- https://www.youtube.com/watch?v=h-TSDMULCf0 (Part 2)
- https://www.youtube.com/watch?v=8DFox0fGjzI (Part 3)
- https://www.youtube.com/watch?v=v-s-Umt1Q0A (Part 4)
- https://www.youtube.com/watch?v=KShybZOMo-8 (Part 5)

### Versioning:

- https://www.youtube.com/watch?v=kkP899WxgzY
- https://community.temporal.io/t/workflow-versioning-strategies/6911
- https://temporal.io/blog/learn-temporal-workflow-versioning-with-free-hands-on-training

### Timeouts:

- https://www.youtube.com/watch?v=JK7WLK3ZSu8

### Debug:

- [Reply workflow](https://github.com/temporalio/samples-java/blob/main/src/test/java/io/temporal/samples/hello/HelloActivityReplayTest.java)
