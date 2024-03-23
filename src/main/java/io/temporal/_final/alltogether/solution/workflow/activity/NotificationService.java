package io.temporal._final.alltogether.solution.workflow.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.model.Account;
import io.temporal.model.TransferRequest;

@ActivityInterface
public interface NotificationService {


    @ActivityMethod
    void accountClosed(Account account);

    @ActivityMethod
    void transferDenied(TransferRequest transferRequest);

    @ActivityMethod
    void transferCompleted(TransferRequest transferRequest);
}
