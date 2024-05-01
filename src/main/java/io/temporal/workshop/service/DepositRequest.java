package io.temporal.workshop.service;

import java.util.Objects;

public final class DepositRequest {
    private String accountId;
    private double amount;

    public DepositRequest() {
    }

    public DepositRequest(String accountId, double amount) {
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
        DepositRequest that = (DepositRequest) obj;
        return Objects.equals(this.accountId, that.accountId)
                && Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId,  amount);
    }

    @Override
    public String toString() {
        return "DepositRequest["
                + "accountId="
                + accountId
                + ", "
                + "amount="
                + amount
                + ']';
    }
}
