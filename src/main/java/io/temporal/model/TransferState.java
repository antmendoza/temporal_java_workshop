package io.temporal.model;

public enum TransferState {
    Approved,
    ApprovalNotRequired,
    ApprovalTimedOut,

    ApprovalRequired,
    ApprovalDenied
}
