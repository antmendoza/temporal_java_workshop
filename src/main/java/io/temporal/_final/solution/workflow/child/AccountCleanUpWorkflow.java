package io.temporal._final.solution.workflow.child;

import io.temporal.model.Account;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AccountCleanUpWorkflow {


    @WorkflowMethod
    void run(Account account);


    static String workflowIdFromAccountId(String value) {
        return "cleanup-account-[" + value + "]";
    }

}

