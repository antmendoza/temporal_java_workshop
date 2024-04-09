package io.temporal.model;

public record Account(String accountId, String customerName, double balance) {


    public Account withdraw(final double amount) {
        return new Account(this.accountId, this.customerName, this.balance - amount);
    }
    public Account deposit(final double amount) {
        return new Account(this.accountId, this.customerName, this.balance + amount);
    }
}
