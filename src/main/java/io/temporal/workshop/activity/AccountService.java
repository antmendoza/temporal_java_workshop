package io.temporal.workshop.activity;

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
