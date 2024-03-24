# Introduction




Temporal is a durable execution system ....

//TODO Diagrams and stuff...
//TODO start with update workflow


## Prerequisites

To persist the state to a file on disk, use --db-filename add `--db-filename my_test.db`


`temporal server start-dev --dynamic-config-value frontend.enableUpdateWorkflowExecution=true`
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

### Timeouts:

- https://www.youtube.com/watch?v=JK7WLK3ZSu8

### Debug:

- [Reply workflow](https://github.com/temporalio/samples-java/blob/main/src/test/java/io/temporal/samples/hello/HelloActivityReplayTest.java)
