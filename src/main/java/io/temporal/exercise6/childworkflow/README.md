# Child workflows

The transfer method takes a list of TransferRequest (instead of only one request), the code 
iterate over each TransferRequest and execute `withdraw` and `deposit`.

Exercise: Modify the code in `io.temporal.exercise10.childworkflow.initial`, to create and execute a child workflow for each TransferRequest
in the list.

You can choose between running child workflows sequentially or in parallel.

### [Child workflow](https://docs.temporal.io/application-development/features?lang=java#child-workflows)


## Code sample

#### Sync invocation
- [Sync execution child workflow](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloChild.java#L98)


#### Async invocation
- [Async execution child workflow](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloChild.java#L89)
- [Parallel execution of activities (same applies to child workflows)](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloParallelActivity.java)