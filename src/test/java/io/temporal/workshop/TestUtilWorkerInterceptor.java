package io.temporal.workshop;

import io.temporal.common.interceptors.WorkerInterceptorBase;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;

public class TestUtilWorkerInterceptor extends WorkerInterceptorBase {

    private final TestUtilInterceptorTracker testUtilInterceptorTracker;

    public TestUtilWorkerInterceptor(TestUtilInterceptorTracker testUtilInterceptorTracker) {
        this.testUtilInterceptorTracker = testUtilInterceptorTracker;
    }

    @Override
    public WorkflowInboundCallsInterceptor interceptWorkflow(WorkflowInboundCallsInterceptor next) {
        return new TestUtilWorkflowInboundCallsInterceptor(next, testUtilInterceptorTracker);
    }
}
