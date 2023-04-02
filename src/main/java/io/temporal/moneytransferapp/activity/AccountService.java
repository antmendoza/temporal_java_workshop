package io.temporal.moneytransferapp.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AccountService {


    @ActivityMethod
    void deposit(DepositRequest depositRequest);

    @ActivityMethod
    void withdraw(WithdrawRequest withdrawRequest);
}
