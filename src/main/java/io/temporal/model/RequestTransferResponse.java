package io.temporal.model;

public class RequestTransferResponse {

    private String operationId;

    public RequestTransferResponse() {

    }


    public RequestTransferResponse(final String operationId) {
        this.operationId = operationId;
    }


    public String getOperationId() {
        return operationId;
    }

}
