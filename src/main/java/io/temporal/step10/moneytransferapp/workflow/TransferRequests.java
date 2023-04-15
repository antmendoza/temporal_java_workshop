package io.temporal.step10.moneytransferapp.workflow;

import java.util.List;

public record TransferRequests(List<TransferRequest>  transferRequests) {
}