package io.temporal.demo_ticarum.workflow.activity;

import io.temporal.demo_ticarum.model.Asignatura;
import io.temporal.demo_ticarum.model.Asignaturas;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AutomatriculaServiceImpl implements AutomatriculaService {
  @Override
  public Asignaturas asignaturasDisponiblesParaAlumno(String alumnoId, String curso) {

    final List asignaturas =
        IntStream.range(0, 10)
            .mapToObj(i -> new Asignatura("asignatura-" + i, "asignatura_" + i + "_curso_" + curso))
            .collect(Collectors.toList());

    return new Asignaturas(asignaturas);
  }
}
