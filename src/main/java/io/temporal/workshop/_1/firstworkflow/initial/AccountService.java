package io.temporal.workshop._1.firstworkflow.initial;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.workshop.service.DepositRequest;
import io.temporal.workshop.service.WithdrawRequest;

@ActivityInterface
public interface AccountService {

    @ActivityMethod
    void deposit(DepositRequest depositRequest);

    @ActivityMethod
    void withdraw(WithdrawRequest withdrawRequest);
}
