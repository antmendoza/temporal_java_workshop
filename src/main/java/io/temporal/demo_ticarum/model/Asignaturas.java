package io.temporal.demo_ticarum.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Asignaturas {

  private List<Asignatura> asignaturas = new ArrayList<>();

  public Asignaturas() {}

  public Asignaturas(List asignaturas) {
    this.asignaturas = asignaturas;
  }

  public List<Asignatura> getAsignaturas() {
    return asignaturas;
  }

  public Asignatura get(String asignaturaId) {
    return asignaturas.stream()
        .filter(asignatura -> asignatura.getId().equals(asignaturaId))
        .findFirst()
        .get();
  }

  @Override
  public String toString() {
    return "Asignaturas{" + "asignaturas=" + asignaturas + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Asignaturas that = (Asignaturas) o;
    return Objects.equals(asignaturas, that.asignaturas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(asignaturas);
  }
}
