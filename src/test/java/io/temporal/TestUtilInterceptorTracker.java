package io.temporal;

import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptor;

import java.util.ArrayList;
import java.util.List;

public class TestUtilInterceptorTracker {
    private final List<WorkflowOutboundCallsInterceptor.ContinueAsNewInput> continueAsNewInvocations =
            new ArrayList<>();

    private final List<WorkflowOutboundCallsInterceptor.CancelWorkflowInput> cancelWorkflow =
            new ArrayList<>();
    private final List<NewWorkflowInvocation> newInvocations = new ArrayList<>();
    private final List<NewSignalInvocation> newSignalInvocation = new ArrayList<>();

    public void recordContinueAsNewInvocation(
            WorkflowOutboundCallsInterceptor.ContinueAsNewInput input) {
        this.continueAsNewInvocations.add(input);
    }

    public void recordNewWorkflowInvocation(NewWorkflowInvocation newWorkflowInvocation) {
        this.newInvocations.add(newWorkflowInvocation);
    }

    public void recordCancelWorkflow(WorkflowOutboundCallsInterceptor.CancelWorkflowInput input) {
        this.cancelWorkflow.add(input);
    }

    public void waitUntilWorkflowIsSignaled(int times) {
        waitUntilTrue(
                new Awaitable(
                        () -> newSignalInvocation.size() == times));
    }

    public void recordNewSignalInvocation(final TestUtilInterceptorTracker.NewSignalInvocation newSignalInvocation) {
        this.newSignalInvocation.add(newSignalInvocation);

    }

    private void waitUntilTrue(Awaitable r) {
        r.returnWhenTrue();
    }

    public static class NewWorkflowInvocation {
        private final String workflowType;
        private final WorkflowInboundCallsInterceptor.WorkflowInput input;

        public NewWorkflowInvocation(
                String workflowType, WorkflowInboundCallsInterceptor.WorkflowInput input) {
            this.workflowType = workflowType;
            this.input = input;
        }


    }

    public static class NewSignalInvocation {

        private final String workflowType;
        private final WorkflowInboundCallsInterceptor.SignalInput input;

        public NewSignalInvocation(
                String workflowType,
                WorkflowInboundCallsInterceptor.SignalInput input) {
            this.workflowType = workflowType;
            this.input = input;
        }

    }

    public static class Awaitable {

        private final Condition condition;

        private Awaitable(Condition condition) {
            this.condition = condition;
        }

        public <T> void returnWhenTrue() {
            while (true) {
                try {
                    final boolean result = this.condition.check();
                    if (result) {
                        return;
                    }
                } catch (Exception e) {
                    // do nothing
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }

        interface Condition {
            boolean check();
        }
    }


}
