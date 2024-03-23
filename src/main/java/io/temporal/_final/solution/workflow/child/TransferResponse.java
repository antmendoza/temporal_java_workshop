package io.temporal._final.solution.workflow.child;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.temporal.model.TransferRequest;

public record TransferResponse(TransferRequest transferRequest, TransferStatus transferStatus) {

    @JsonIgnore
    public boolean isApproved() {
        return TransferStatus.Approved.equals(this.transferStatus());
    }

}
