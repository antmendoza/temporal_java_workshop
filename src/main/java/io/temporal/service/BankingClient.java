package io.temporal.service;

public class BankingClient {

    private static void randomSleep() {
        try {
            long millis = (long) (Math.random() * 1000);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void withdraw(WithdrawRequest withdrawRequest) {
        System.out.println("Withdraw init: " + withdrawRequest);
        randomSleep();
        System.out.println("Withdraw end: " + withdrawRequest);
    }

    public void deposit(DepositRequest depositRequest) {
        System.out.println("Deposit init: " + depositRequest);
        randomSleep();
        System.out.println("Deposit end: " + depositRequest);
    }
}
