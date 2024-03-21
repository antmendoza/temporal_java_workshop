package io.temporal.exercise10.alltogether.solution.workflow;

import io.temporal.workflow.*;

@WorkflowInterface
public interface AccountWorkflow {


    @WorkflowMethod
    void open(Account account);


    @SignalMethod
    void requestTransfer(Transfer transferRequest);

    @UpdateValidatorMethod(updateName = "updateCustomer")
    void validateUpdateCustomer(String  newCustomerIdValue);

    @UpdateMethod
    UpdateCustomerResponse updateCustomer(String newCustomerIdValue);

    @UpdateMethod
    String closeAccount();

    static String workflowIdFromAccountId(String value){
        return "account["+value+"]";
    }

}

