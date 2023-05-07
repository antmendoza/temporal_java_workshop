package io.temporal.demo4.queryworklow.solution.workflow;

public enum TRANSFER_STATUS {
  INITIATED,
  WAITING_APPROVAL,
  APPROVED,
  COMPLETED,
  DENIED,
}
