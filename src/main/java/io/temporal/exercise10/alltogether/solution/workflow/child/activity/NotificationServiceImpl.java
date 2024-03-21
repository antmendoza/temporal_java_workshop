package io.temporal.exercise10.alltogether.solution.workflow.child.activity;

import io.temporal.exercise10.alltogether.solution.workflow.Account;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class NotificationServiceImpl implements
        NotificationService {


    private final Logger log = Workflow.getLogger(NotificationServiceImpl.class.getSimpleName());

    @Override
    public void sendNotificationClosingAccount(final Account account) {

        log.info("Sending notification to customer " + account.customerId()+ " \n" +
                "We have closed your account with Id... "+ account.accountId());
    }
}
