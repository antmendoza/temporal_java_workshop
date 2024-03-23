package io.temporal;

import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptorBase;

public class TestUtilWorkflowOutboundCallsInterceptor extends WorkflowOutboundCallsInterceptorBase {
    private TestUtilInterceptorTracker testUtilInterceptorTracker;

    public TestUtilWorkflowOutboundCallsInterceptor(
            WorkflowOutboundCallsInterceptor outboundCalls,
            TestUtilInterceptorTracker testUtilInterceptorTracker) {
        super(outboundCalls);
        this.testUtilInterceptorTracker = testUtilInterceptorTracker;
    }

    @Override
    public void continueAsNew(ContinueAsNewInput input) {
        this.testUtilInterceptorTracker.recordContinueAsNewInvocation(input);
        super.continueAsNew(input);
    }

    @Override
    public CancelWorkflowOutput cancelWorkflow(CancelWorkflowInput input) {
        this.testUtilInterceptorTracker.recordCancelWorkflow(input);
        return super.cancelWorkflow(input);
    }
}
