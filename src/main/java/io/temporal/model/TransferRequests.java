package io.temporal.model;

import java.util.List;
import java.util.Objects;

public class TransferRequests {

  private List<TransferRequest> transferRequests;

  public TransferRequests() {}

  public TransferRequests(List<TransferRequest> transferRequests) {
    this.transferRequests = transferRequests;
  }

  public List<TransferRequest> transferRequests() {
    return transferRequests;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TransferRequests that = (TransferRequests) o;
    return Objects.equals(transferRequests, that.transferRequests);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transferRequests);
  }

  @Override
  public String toString() {
    return "TransferRequests{" + "transferRequests=" + transferRequests + '}';
  }
}
