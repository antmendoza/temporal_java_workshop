package io.temporal.step30.moneytransferapp.workflow;

public enum TRANSFER_STATUS {
  WAITING_APPROVAL,
  APPROVED,
  DENIED,
  INITIATED
}
