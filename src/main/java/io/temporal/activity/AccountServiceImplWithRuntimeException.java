package io.temporal.activity;

import io.temporal.service.BankingClient;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class AccountServiceImplWithRuntimeException implements AccountService {

    public static final int VALUE_TO_THROW_EXCEPTION = 1_111;

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


        //For
        if (depositRequest.amount() == VALUE_TO_THROW_EXCEPTION) {

            log.info("depositRequest.amount() ==  : " + VALUE_TO_THROW_EXCEPTION + "; throwing exception ... ");

            int attend = Activity.getExecutionContext().getInfo().getAttempt();
            log.info("Attend number : " + attend);

            if (attend <= 4) {
                String message = "Error: Can not reach service...Number of attend: " + attend;
                throw new RuntimeException(message);
            }
        }

        this.bankingClient.deposit(depositRequest);
        log.info("End deposit : " + depositRequest);
    }

}
