package io.temporal.demo_ticarum.workflow.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.demo_ticarum.model.Asignaturas;

@ActivityInterface
public interface AutomatriculaService {
  Asignaturas asignaturasDisponiblesParaAlumno(String alumnoId, String curso);
}
