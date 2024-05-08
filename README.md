# Introduction

This workshop is an introduction to implement [Temporal](https://temporal.io/) workflows in Java. Across several and iterative exercises, 
it introduces the main Temporal concepts like: 
- [workflows](https://docs.temporal.io/workflows) 
- [activities](https://docs.temporal.io/activities)
- [signals](https://docs.temporal.io/workflows#signal)
- [queries](https://docs.temporal.io/workflows#query)
- [update workflow](https://docs.temporal.io/workflows#update)
- [search attributes](https://docs.temporal.io/visibility#search-attribute)
- [child workflows](https://docs.temporal.io/encyclopedia/child-workflows)

## Prerequisites

See [prepare-your-environment.md](./prepare-your-environment.md).

## Modules:

### demo

### [Firstworkflow](./src/main/java/io/temporal/workshop/_1/firstworkflow/README.md)

Step by step, how to create a workflow with activities in Java.

### [Signal](./src/main/java/io/temporal/workshop/_2/signal/README.md)

Shows how to use [Workflow Signal](https://docs.temporal.io/workflows#signal), to send an asynchronous message to a workflow execution.

### [Query](./src/main/java/io/temporal/workshop/_3/query/README.md)

Shows how to use [Workflow Query](https://docs.temporal.io/workflows#query), to get the workflow internal state.


### [Update](./src/main/java/io/temporal/workshop/_4/update/README.md)

Shows how to use [Workflow Update](https://docs.temporal.io/workflows#update), to change the workflow state and waits until the update method completes.

### [Temporal + Spring Boot](./src/main/java/io/temporal/workshop/_final/README.md)

Step by step, create an application for managing accounts and transfers using Temporal and Spring Boot.

![](doc_img/img_9.png)


## Resources:

- [Learn Temporal](https://learn.temporal.io/)
- [Documentation](https://docs.temporal.io/)

