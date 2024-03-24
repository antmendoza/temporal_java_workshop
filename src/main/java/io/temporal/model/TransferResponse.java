package io.temporal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TransferResponse(TransferRequest transferRequest, TransferState transferState) {

    @JsonIgnore
    public boolean isApproved() {
        return TransferState.Approved.equals(this.transferState()) ||
        TransferState.ApprovalNotRequired.equals(this.transferState())

        ;
    }

}
