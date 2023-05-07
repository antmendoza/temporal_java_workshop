package io.temporal.model;

import java.util.Objects;

public final class TransferRequest {
  private String fromAccountId;
  private String toAccountId;
  private String referenceId;
  private double amount;

  public TransferRequest() {}

  public TransferRequest(
      String fromAccountId, String toAccountId, String referenceId, double amount) {
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.referenceId = referenceId;
    this.amount = amount;
  }

  public String fromAccountId() {
    return fromAccountId;
  }

  public String toAccountId() {
    return toAccountId;
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
    TransferRequest that = (TransferRequest) obj;
    return Objects.equals(this.fromAccountId, that.fromAccountId)
        && Objects.equals(this.toAccountId, that.toAccountId)
        && Objects.equals(this.referenceId, that.referenceId)
        && Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromAccountId, toAccountId, referenceId, amount);
  }

  @Override
  public String toString() {
    return "TransferRequest["
        + "fromAccountId="
        + fromAccountId
        + ", "
        + "toAccountId="
        + toAccountId
        + ", "
        + "referenceId="
        + referenceId
        + ", "
        + "amount="
        + amount
        + ']';
  }
}
