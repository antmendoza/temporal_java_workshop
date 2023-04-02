package io.temporal.moneytransferapp.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.moneytransferapp.activity.AccountService;
import io.temporal.moneytransferapp.activity.DepositRequest;
import io.temporal.moneytransferapp.activity.WithdrawRequest;
import io.temporal.workflow.Workflow;

import java.time.Duration;

/**
 * GreetingWorkflow implementation that calls GreetingsActivities#composeGreeting.
 */
public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    //private final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class.getSimpleName());

    public static final String TASK_QUEUE = "MoneyTransfer";


    @Override
    public String transfer(String fromAccountId, String toAccountId, String referenceId, double amount) {
        System.out.println("Hello from JOTB23");


        final AccountService accountService = Workflow.newActivityStub(AccountService.class, ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(1))
                .build());

        accountService.withdraw(new WithdrawRequest(fromAccountId, referenceId, amount));
        accountService.deposit(new DepositRequest(toAccountId, referenceId, amount));

        return "some-transference-id";
    }
}
