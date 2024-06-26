package io.temporal.workshop.activity;

import io.temporal.workshop.model.Account;
import io.temporal.workshop.model.TransferRequest;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class NotificationServiceImpl implements
        NotificationService {

    private final Logger log = Workflow.getLogger(NotificationServiceImpl.class.getSimpleName());

    @Override
    public void accountClosed(final Account account) {
        log.info("accountClosed: Sending notification" + account);
    }

    @Override
    public void operationDenied(final TransferRequest transferRequest) {
        log.info("transferDenied: Sending notification" + transferRequest);

    }

    @Override
    public void transferCompleted(final TransferRequest transferRequest) {
        log.info("transferCompleted: Sending notification" + transferRequest);

    }
}
