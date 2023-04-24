package io.temporal.demo_ticarum.workflow.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.demo_ticarum.model.Asignatura;
import io.temporal.demo_ticarum.workflow.AutomatriculaRequest;
import java.util.List;

@ActivityInterface
public interface EmailService {
  void enviarEmailRecordatorio(String alumnoId);

  void enviarNotificacionPago(
      List<Asignatura> asignaturasElegidas, AutomatriculaRequest automatriculaRequest);
}
