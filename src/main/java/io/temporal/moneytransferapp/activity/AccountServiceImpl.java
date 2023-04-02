package io.temporal.moneytransferapp.activity;

public class AccountServiceImpl implements AccountService {

    private final BankingClient bankingClient;

    public AccountServiceImpl(BankingClient bankingClient) {
        this.bankingClient = bankingClient;
    }


    @Override
    public void withdraw(WithdrawRequest withdrawRequest) {
        this.bankingClient.withdraw(withdrawRequest);
    }

    @Override
    public void deposit(DepositRequest depositRequest) {
        this.bankingClient.deposit(depositRequest);


    }


}
