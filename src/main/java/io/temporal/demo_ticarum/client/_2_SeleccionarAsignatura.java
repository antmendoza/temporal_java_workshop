package io.temporal.demo_ticarum.client;

import io.temporal.client.WorkflowClient;
import io.temporal.demo_ticarum.DatosPrueba;
import io.temporal.demo_ticarum.workflow.Automatricula;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class _2_SeleccionarAsignatura {

  public static void main(String[] args) {
    // Get a Workflow service stub.
    final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    final WorkflowClient client = WorkflowClient.newInstance(service);

    Automatricula automatricula =
        client.newWorkflowStub(Automatricula.class, DatosPrueba.getWorkflowId());

    automatricula.seleccionarAsignatura("asignatura-0");

    automatricula.seleccionarAsignatura("asignatura-1");

    automatricula.seleccionarAsignatura("asignatura-2");
  }
}
