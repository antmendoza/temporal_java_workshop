/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.temporal.demo_ticarum.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.demo_ticarum.model.Asignatura;
import io.temporal.demo_ticarum.model.Asignaturas;
import io.temporal.demo_ticarum.workflow.activity.AutomatriculaService;
import io.temporal.demo_ticarum.workflow.activity.EmailService;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class AutomatriculaImpl implements Automatricula {

  private final Logger log = Workflow.getLogger(AutomatriculaImpl.class.getSimpleName());
  private final AutomatriculaService automatriculaService =
      Workflow.newActivityStub(
          AutomatriculaService.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

  private final EmailService enviarEmail =
      Workflow.newActivityStub(
          EmailService.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());
  private final List<Asignatura> asignaturasElegidas = new ArrayList<>();
  private boolean finalizar = false;
  private Asignaturas asignaturaDisponibles;
  private AutomatriculaRequest automatriculaRequest;

  @Override
  public void start(AutomatriculaRequest automatriculaRequest) {

    this.automatriculaRequest = automatriculaRequest;
    log.info("empezando automatricula " + automatriculaRequest);

    asignaturaDisponibles =
        automatriculaService.asignaturasDisponiblesParaAlumno(
            automatriculaRequest.getAlumnoId(), automatriculaRequest.getCurso());

    // si el alumno no confirma la matricula en 2 dias le enviamos un recordatorio
    Workflow.newTimer(Duration.ofDays(2))
        .thenApply(
            result -> {
              log.info("enviando email " + automatriculaRequest);
              enviarEmail.enviarEmailRecordatorio(automatriculaRequest.getAlumnoId());
              return result;
            });

    // esperar hasta pulsar finalizar
    Workflow.await(() -> this.finalizar);

    enviarEmail.enviarNotificacionPago(asignaturasElegidas, automatriculaRequest);
  }

  @Override
  public Asignaturas getAsignaturasDisponibles() {
    return asignaturaDisponibles;
  }

  @Override
  public List<Asignatura> getAsignaturasElegidas() {
    return asignaturasElegidas;
  }

  @Override
  public void seleccionarAsignatura(String asignaturaId) {

    Workflow.await(() -> asignaturaDisponibles != null); // FIXME

    log.info("añadiendo asignatura " + asignaturaId);
    final Asignatura asignatura = asignaturaDisponibles.get(asignaturaId);
    if (!this.asignaturasElegidas.contains(asignatura)) {
      this.asignaturasElegidas.add(asignatura);
      log.info("asignatura añadida " + asignaturaId);
    }
  }

  @Override
  public void borrarAsignatura(String asignaturaId) {
    Workflow.await(() -> asignaturaDisponibles != null); // FIXME

    log.info("borrando asignatura " + asignaturaId);
    this.asignaturasElegidas.remove(asignaturaDisponibles.get(asignaturaId));
  }

  @Override
  public void finalizar() {

    log.info("finalizar automatricula " + automatriculaRequest);
    this.finalizar = true;
  }
}
