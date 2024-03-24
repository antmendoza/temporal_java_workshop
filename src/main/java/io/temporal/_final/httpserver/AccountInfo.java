package io.temporal._final.httpserver;

import io.temporal.model.Account;

public record AccountInfo(String workflowId,  Account account) {
}
