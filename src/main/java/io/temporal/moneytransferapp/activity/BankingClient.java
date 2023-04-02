package io.temporal.moneytransferapp.activity;

public class BankingClient {

    public void withdraw(WithdrawRequest withdrawRequest) {
        System.out.println("Withdraw: " + withdrawRequest);
    }


    public void deposit(DepositRequest depositRequest) {
        throw new NullPointerException("whatever.... ");

        //System.out.println("Deposit: " + depositRequest);
    }
}
