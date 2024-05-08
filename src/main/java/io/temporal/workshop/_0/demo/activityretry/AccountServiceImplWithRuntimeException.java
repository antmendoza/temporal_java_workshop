package io.temporal.workshop._0.demo.activityretry;

import io.temporal.workshop.service.DepositRequest;
import io.temporal.workshop.service.WithdrawRequest;
import io.temporal.workshop.activity.AccountService;
import io.temporal.activity.Activity;
import io.temporal.workshop.service.BankingClient;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class AccountServiceImplWithRuntimeException implements AccountService {

    private final Logger log = Workflow.getLogger(AccountServiceImplWithRuntimeException.class.getSimpleName());

    private final BankingClient bankingClient;

    public AccountServiceImplWithRuntimeException(BankingClient bankingClient) {
        this.bankingClient = bankingClient;
    }

    @Override
    public void withdraw(WithdrawRequest withdrawRequest) {
        log.info("Init withdraw : " + withdrawRequest);
        this.bankingClient.withdraw(withdrawRequest);
        log.info("End withdraw : " + withdrawRequest);
    }

    @Override
    public void deposit(DepositRequest depositRequest) {
        log.info("Init deposit : " + depositRequest);
        int attempt = Activity.getExecutionContext().getInfo().getAttempt();
        log.info("Attempt number : " + attempt);


        //throwing fake error, after some retries we can fix this and re-start the worker
        if (depositRequest.amount() != 0) {

            String message = "Error: Can not reach service...Number of attempt: " + attempt;
            //Imagine an error is returned by an external service (API, db is down), and this invocation fails
            // and this method throw this exception
            // TODO comment this line
            throw new RuntimeException(message);
        }

        this.bankingClient.deposit(depositRequest);
        log.info("End deposit : " + depositRequest);
    }

}
