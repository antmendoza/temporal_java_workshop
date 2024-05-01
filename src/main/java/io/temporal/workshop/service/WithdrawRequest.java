package io.temporal.workshop.service;

import java.util.Objects;

public final class WithdrawRequest {
    private String accountId;
    private double amount;

    public WithdrawRequest() {
    }

    public WithdrawRequest(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String accountId() {
        return accountId;
    }


    public double amount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        WithdrawRequest that = (WithdrawRequest) obj;
        return Objects.equals(this.accountId, that.accountId)
                && Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, amount);
    }

    @Override
    public String toString() {
        return "WithdrawRequest["
                + "accountId="
                + accountId
                + ", "
                + "amount="
                + amount
                + ']';
    }
}
