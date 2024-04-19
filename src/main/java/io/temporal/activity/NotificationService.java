package io.temporal.activity;

import io.temporal.model.Account;
import io.temporal.model.TransferRequest;

@ActivityInterface
public interface NotificationService {


    @ActivityMethod
    void accountClosed(Account account);

    @ActivityMethod
    void operationDenied(TransferRequest transferRequest);

    @ActivityMethod
    void transferCompleted(TransferRequest transferRequest);
}
