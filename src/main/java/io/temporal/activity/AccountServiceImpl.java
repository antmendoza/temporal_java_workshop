package io.temporal.activity;

import io.temporal.service.BankingClient;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;
import io.temporal.workflow.Workflow;
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
