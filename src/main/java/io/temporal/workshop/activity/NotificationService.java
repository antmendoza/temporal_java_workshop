package io.temporal.workshop.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.workshop.model.Account;
import io.temporal.workshop.model.TransferRequest;

@ActivityInterface
public interface NotificationService {


    @ActivityMethod
    void accountClosed(Account account);

    @ActivityMethod
    void operationDenied(TransferRequest transferRequest);

    @ActivityMethod
    void transferCompleted(TransferRequest transferRequest);
}
