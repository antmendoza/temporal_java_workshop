package io.temporal.workshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TransferResponse(TransferRequest transferRequest, TransferStatus transferStatus) {

    @JsonIgnore
    public boolean isApproved() {
        return TransferStatus.Approved.equals(this.transferStatus()) ||
        TransferStatus.ApprovalNotRequired.equals(this.transferStatus());
    }


    public String toReadableString() {
        return System.lineSeparator() +
                 transferRequest.toReadableString()
                + System.lineSeparator() +
                "   Status = " + transferStatus;
    }
}
