package io.temporal.workshop.model;

public record TransferRequest(
        String fromAccountId, String toAccountId, double amount) {


    public String toReadableString() {
        return  "   FromAccountId = " + fromAccountId
                +System.lineSeparator()+
                "   ToAccountId = " + toAccountId
                +System.lineSeparator()+
                "   Amount=" + amount;
    }
}
