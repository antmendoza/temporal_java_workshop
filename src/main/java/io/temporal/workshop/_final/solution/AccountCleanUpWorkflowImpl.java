package io.temporal.workshop._final.solution;

import io.temporal.workshop._final.AccountCleanUpWorkflow;
import io.temporal.workshop.activity.NotificationService;
import io.temporal.workshop.model.Account;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class AccountCleanUpWorkflowImpl implements
        AccountCleanUpWorkflow {

    private final Logger log = Workflow.getLogger(AccountCleanUpWorkflowImpl.class.getSimpleName());


    @Override
    public void run(final Account account) {

        log.info("Init for account: "+account);

        final NotificationService notificationService = Workflow.newActivityStub(NotificationService.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(3))
                        .build());


        //Only to demonstrate that the child workflow can continue running, independently of the parent
        Workflow.sleep(Duration.ofSeconds(30));

        notificationService.accountClosed(account);

        log.info("Completed for account: "+account);

    }


}
