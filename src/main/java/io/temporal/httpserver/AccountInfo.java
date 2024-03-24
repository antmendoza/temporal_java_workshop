package io.temporal.httpserver;

import io.temporal.model.Account;

public record AccountInfo(String workflowId,  Account account) {
}
