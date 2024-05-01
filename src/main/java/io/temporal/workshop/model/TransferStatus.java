package io.temporal.workshop.model;

public enum TransferStatus {
    Approved,
    ApprovalNotRequired,
    TimedOut,
    ApprovalRequired,
    Denied
}
