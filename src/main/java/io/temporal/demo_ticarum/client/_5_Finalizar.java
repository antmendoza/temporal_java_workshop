package io.temporal.demo_ticarum.client;

import io.temporal.client.WorkflowClient;
import io.temporal.demo_ticarum.DatosPrueba;
import io.temporal.demo_ticarum.workflow.Automatricula;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class _5_Finalizar {

  public static void main(String[] args) {
    // Get a Workflow service stub.
    final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    final WorkflowClient client = WorkflowClient.newInstance(service);

    Automatricula automatricula =
        client.newWorkflowStub(Automatricula.class, DatosPrueba.getWorkflowId());

    automatricula.finalizar();
  }
}
