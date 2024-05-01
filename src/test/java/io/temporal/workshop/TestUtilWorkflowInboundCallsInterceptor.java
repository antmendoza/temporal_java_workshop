package io.temporal.workshop;

import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor;
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptorBase;
import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptor;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInfo;

public class TestUtilWorkflowInboundCallsInterceptor extends WorkflowInboundCallsInterceptorBase {
    private TestUtilInterceptorTracker testUtilInterceptorTracker;

    public TestUtilWorkflowInboundCallsInterceptor(
            WorkflowInboundCallsInterceptor next, TestUtilInterceptorTracker testUtilInterceptorTracker) {
        super(next);
        this.testUtilInterceptorTracker = testUtilInterceptorTracker;
    }

    @Override
    public void init(WorkflowOutboundCallsInterceptor outboundCalls) {
        super.init(
                new TestUtilWorkflowOutboundCallsInterceptor(outboundCalls, testUtilInterceptorTracker));
    }

    @Override
    public WorkflowOutput execute(WorkflowInput input) {

        final WorkflowInfo workflowInfo = Workflow.getInfo();
        final String workflowType = workflowInfo.getWorkflowType();
        final String workflowId = workflowInfo.getWorkflowId();
        testUtilInterceptorTracker.recordNewWorkflowInvocation(
                new TestUtilInterceptorTracker.NewWorkflowInvocation(workflowType, input));
        final WorkflowOutput output = super.execute(input);

        testUtilInterceptorTracker.recordWorkflowClosed(
                new TestUtilInterceptorTracker.NewWorkflowClosed(workflowInfo, output));

        return output;
    }

    @Override
    public void handleSignal(SignalInput input) {

        WorkflowInfo info = Workflow.getInfo();
        String workflowType = info.getWorkflowType();
        testUtilInterceptorTracker.recordNewSignalInvocation(
                new TestUtilInterceptorTracker.NewSignalInvocation(workflowType, input));

        super.handleSignal(input);
    }

    @Override
    public QueryOutput handleQuery(QueryInput input) {
        return super.handleQuery(input);
    }
}
