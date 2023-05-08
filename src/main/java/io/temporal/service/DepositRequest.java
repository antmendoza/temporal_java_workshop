package io.temporal.service;

import java.util.Objects;

public final class DepositRequest {
  private String accountId;
  private String referenceId;
  private double amount;

  public DepositRequest() {}

  public DepositRequest(String accountId, String referenceId, double amount) {
    this.accountId = accountId;
    this.referenceId = referenceId;
    this.amount = amount;
  }

  public String accountId() {
    return accountId;
  }

  public String referenceId() {
    return referenceId;
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
        && Objects.equals(this.referenceId, that.referenceId)
        && Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, referenceId, amount);
  }

  @Override
  public String toString() {
    return "DepositRequest["
        + "accountId="
        + accountId
        + ", "
        + "referenceId="
        + referenceId
        + ", "
        + "amount="
        + amount
        + ']';
  }
}
