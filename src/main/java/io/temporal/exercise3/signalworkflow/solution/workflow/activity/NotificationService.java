package io.temporal.exercise3.signalworkflow.solution.workflow.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface NotificationService {

  @ActivityMethod
  void notifyCustomerTransferNotApproved();

  @ActivityMethod
  void notifyCustomerTransferDone();
}
