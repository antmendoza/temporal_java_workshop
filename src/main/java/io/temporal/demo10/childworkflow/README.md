# Child workflows

Exercise: Modify code, in io.temporal.demo10.childworkflow.initial, to execute a child for each item in the list 
transferRequests .
You can choose between running child workflows sequentially or in parallel.


### [Child workflow](https://docs.temporal.io/application-development/features?lang=java#child-workflows)

### Steps

- (Create interfaz)[https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloChild.java#L70]
- (Create implementation)[https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloChild.java#L113]
- Move login to invoque withdraw and transfer methods to the child workflow implementation
- (Register child workflow implementation with the worker)[https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloChild.java#L151] 


## Code sample
#### Blocking invocation
- Sample code child workflow
  - https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloChild.java#L89
  - https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloChild.java#L98


#### Async invocation
- [Async execution child workflow](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloChild.java#L100)
- [Parallel execution of activities (sema applies to child workflows)](https://github.com/temporalio/samples-java/blob/main/src/main/java/io/temporal/samples/hello/HelloParallelActivity.java)