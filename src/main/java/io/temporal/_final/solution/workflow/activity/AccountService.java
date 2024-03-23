package io.temporal._final.solution.workflow.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.service.DepositRequest;
import io.temporal.service.WithdrawRequest;

@ActivityInterface
public interface AccountService {

    @ActivityMethod
    void deposit(DepositRequest depositRequest);

    @ActivityMethod
    void withdraw(WithdrawRequest withdrawRequest);
}
