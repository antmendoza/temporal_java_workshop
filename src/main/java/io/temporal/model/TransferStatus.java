package io.temporal.model;

public enum TransferStatus {
    Approved,
    ApprovalNotRequired,
    TimedOut,
    ApprovalRequired,
    Denied
}
