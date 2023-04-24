package io.temporal.demo_ticarum.workflow.activity;

import io.temporal.demo_ticarum.model.Asignatura;
import io.temporal.demo_ticarum.workflow.AutomatriculaRequest;
import java.util.List;

public class EmailServiceImpl implements EmailService {
  @Override
  public void enviarEmailRecordatorio(String alumnoId) {
    System.out.println("Enviar recordatorio alumno realizar matrícula" + alumnoId);
  }

  @Override
  public void enviarNotificacionPago(
      List<Asignatura> asignaturasElegidas, AutomatriculaRequest automatriculaRequest) {
    System.out.println("Enviar enviarNotificacionPago alumno matrícula" + automatriculaRequest);
  }
}
