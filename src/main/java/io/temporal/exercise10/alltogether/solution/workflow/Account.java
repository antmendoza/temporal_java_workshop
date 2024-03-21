package io.temporal.exercise10.alltogether.solution.workflow;

public record Account(String accountId, String customerId, double amount){

    public Account subtract(final double amount) {
        return new Account(this.accountId, this.customerId, this.amount - amount ) ;
    }
}
