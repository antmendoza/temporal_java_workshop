package io.temporal.model;

public record Account(String accountId, String customerName, double balance) {

    public Account subtract(final double balance) {
        return new Account(this.accountId, this.customerName, this.balance - balance);
    }
}
