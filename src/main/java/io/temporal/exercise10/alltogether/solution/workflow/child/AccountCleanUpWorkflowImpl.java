package io.temporal.exercise10.alltogether.solution.workflow.child;

import io.temporal.activity.ActivityOptions;
import io.temporal.exercise10.alltogether.solution.workflow.Account;
import io.temporal.exercise10.alltogether.solution.workflow.child.activity.NotificationService;
import io.temporal.model.TransferRequest;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class AccountCleanUpWorkflowImpl implements
        AccountCleanUpWorkflow {

    private final Logger log = Workflow.getLogger(AccountCleanUpWorkflowImpl.class.getSimpleName());


    @Override
    public void run(final Account account) {

        final  NotificationService notificationService = Workflow.newActivityStub(NotificationService.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(3))
                        .build());


        //This is just to demonstrate that the child workflow run belong, and independently the parent
        Workflow.sleep(Duration.ofSeconds(5));


        notificationService.sendNotificationClosingAccount(account);

        log.info("sendNotificationClosingAccount  done for account: " +account);
    }




}
