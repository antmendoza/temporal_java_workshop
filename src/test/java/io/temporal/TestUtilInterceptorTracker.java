package io.temporal;

import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptor;
import io.temporal.workflow.WorkflowInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestUtilInterceptorTracker {
    private final List<WorkflowOutboundCallsInterceptor.ContinueAsNewInput> continueAsNewInvocations =
            new ArrayList<>();

    private final List<WorkflowOutboundCallsInterceptor.CancelWorkflowInput> cancelWorkflow =
            new ArrayList<>();
    private final List<NewWorkflowInvocation> newInvocations = new ArrayList<>();
    private final List<NewSignalInvocation> newSignalInvocation = new ArrayList<>();


    private final List<NewWorkflowClosed> closedWorkflows = new ArrayList<>();


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


    public void waitUntilWorkflowIsClosed(String workflowId) {
        waitUntilTrue(
                new Awaitable(
                        () -> {
                            return closedWorkflows.stream()
                                    .filter((closed) -> Objects.equals(closed.workflowInfo.getWorkflowId(), workflowId)).count() > 0;
                        }));

    }



    public void recordNewSignalInvocation(final TestUtilInterceptorTracker.NewSignalInvocation newSignalInvocation) {
        this.newSignalInvocation.add(newSignalInvocation);

    }

    public static void waitUntilTrue(Awaitable r) {
        waitUntilTrue(r,1000);
    }


    public static void waitUntilTrue(Awaitable r, int timeout) {
        r.returnWhenTrue(timeout);
    }

    public void recordWorkflowClosed(final NewWorkflowClosed newWorkflowClosed) {
        this.closedWorkflows.add(newWorkflowClosed);
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

    public static class NewWorkflowClosed {
        private final WorkflowInfo workflowInfo;
        private final WorkflowInboundCallsInterceptor.WorkflowOutput output;

        public NewWorkflowClosed(
                final WorkflowInfo workflowInfo,
                final WorkflowInboundCallsInterceptor.WorkflowOutput output) {
            this.workflowInfo = workflowInfo;
            this.output = output;
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

        public Awaitable(Condition condition) {
            this.condition = condition;
        }


        public <T> void returnWhenTrue(int timeoutMs) {

            final int sleepMs = 100;
            final int iterations = timeoutMs / sleepMs;


            for (int i = 0; i < iterations; i++) {
                try {
                    final boolean result = this.condition.check();
                    if (result) {
                        return;
                    }
                } catch (Exception e) {
                    // do nothing
                }

                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                }
            }

            throw new RuntimeException("Condition not satisfied after " +  timeoutMs + " ms");

        }

        public interface Condition {
            boolean check();
        }
    }


}
