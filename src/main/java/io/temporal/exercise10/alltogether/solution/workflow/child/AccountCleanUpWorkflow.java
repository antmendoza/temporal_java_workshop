package io.temporal.exercise10.alltogether.solution.workflow.child;

import io.temporal.exercise10.alltogether.solution.workflow.Account;
import io.temporal.workflow.*;

@WorkflowInterface
public interface AccountCleanUpWorkflow {


    @WorkflowMethod
    void run(Account account);


    static String workflowIdFromAccountId(String value){
        return "cleanup-account-["+value+"]";
    }

}

