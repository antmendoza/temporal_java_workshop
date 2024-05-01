package io.temporal.workshop._1.firstworkflow.initial;

import io.temporal.workflow.Workflow;
import io.temporal.workshop.service.BankingClient;
import io.temporal.workshop.service.DepositRequest;
import io.temporal.workshop.service.WithdrawRequest;
import org.slf4j.Logger;

public class AccountServiceImpl implements AccountService {

    private final Logger log = Workflow.getLogger(AccountServiceImpl.class.getSimpleName());

    private final BankingClient bankingClient;

    public AccountServiceImpl(BankingClient bankingClient) {
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
        this.bankingClient.deposit(depositRequest);
        log.info("End deposit : " + depositRequest);
    }
}
