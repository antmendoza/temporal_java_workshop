package io.temporal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TransferResponse(TransferRequest transferRequest, TransferStatus transferStatus) {

    @JsonIgnore
    public boolean isApproved() {
        return TransferStatus.Approved.equals(this.transferStatus());
    }

}
