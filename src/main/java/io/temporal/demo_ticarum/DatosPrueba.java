package io.temporal.demo_ticarum;

import io.temporal.demo_ticarum.workflow.AutomatriculaRequest;

public class DatosPrueba {
  static AutomatriculaRequest automatriculaRequest =
      new AutomatriculaRequest("alumno_ID_57", "2_TELECO");

  public static AutomatriculaRequest getRequest() {
    return automatriculaRequest;
  }

  public static String getWorkflowId() {
    return "Automatricula_"
        + automatriculaRequest.getAlumnoId()
        + "_"
        + automatriculaRequest.getCurso();
  }
}
